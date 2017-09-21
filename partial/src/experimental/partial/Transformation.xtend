package experimental.partial

import experimentalCorrespondence.ExperimentalCorrespondencePackage
import experimentalCorrespondence.TransformationModel
import experimentalSource.ExperimentalSourcePackage
import experimentalTarget.ExperimentalTargetPackage
import org.eclipse.emf.ecore.resource.Resource
import org.eclipse.viatra.query.runtime.api.ViatraQueryEngine
import org.eclipse.viatra.transformation.evm.specific.Lifecycles
import org.eclipse.viatra.transformation.evm.specific.crud.CRUDActivationStateEnum
import org.eclipse.viatra.transformation.evm.specific.resolver.InvertedDisappearancePriorityConflictResolver
import org.eclipse.viatra.transformation.runtime.emf.modelmanipulation.IModelManipulations
import org.eclipse.viatra.transformation.runtime.emf.modelmanipulation.SimpleModelManipulations
import org.eclipse.viatra.transformation.runtime.emf.rules.eventdriven.EventDrivenTransformationRuleFactory
import org.eclipse.viatra.transformation.runtime.emf.transformation.eventdriven.EventDrivenTransformation

class Transformation {

	/* Transformation-related extensions */
	extension EventDrivenTransformation transformation

	/* Transformation rule-related extensions */
	extension EventDrivenTransformationRuleFactory = new EventDrivenTransformationRuleFactory
	extension IModelManipulations manipulation

	protected ViatraQueryEngine engine
	protected Resource resource
	// protected BatchTransformationRule<?,?> exampleRule
	val extension PartialQueries queries = PartialQueries.instance

	val extension ExperimentalSourcePackage sourcePackage = ExperimentalSourcePackage.eINSTANCE
	val extension ExperimentalTargetPackage targetPackage = ExperimentalTargetPackage.eINSTANCE
	val extension ExperimentalCorrespondencePackage corrPackage = ExperimentalCorrespondencePackage.eINSTANCE

	val TransformationModel corr

	protected TransformationHandler handler

	new(TransformationModel corr, ViatraQueryEngine engine) {
		this.corr = corr
		handler = new TransformationHandler(corr);
		this.engine = engine
		prepare(engine)
		createTransformation
	}

	public def execute() {
		println('''Executing transformation...''')
		/* Clear output & trace model for batch transformation**/
		transformation.executionSchema.startUnscheduledExecution
		/* Fire transformation rules**/
	}

	private def createTransformation() {
		// Initialize model manipulation API
		this.manipulation = new SimpleModelManipulations(engine)

		// Initialize event-driven transformation
		val fixedPriorityResolver = new InvertedDisappearancePriorityConflictResolver
		fixedPriorityResolver.setPriority(sourceRule.ruleSpecification, 1)
		fixedPriorityResolver.setPriority(targetRule.ruleSpecification, 2)

		transformation = EventDrivenTransformation.forEngine(engine).setConflictResolver(fixedPriorityResolver).addRule(
			sourceRule).addRule(targetRule).build
	}

	val sourceRule = createRule.precondition(SourceMatcher.querySpecification).action(CRUDActivationStateEnum.CREATED) [
		handler.sourceMatch(parent, entry, correspondence)
	].action(CRUDActivationStateEnum.UPDATED) [
		throw new IllegalStateException('''unexpected source update''')
	].action(CRUDActivationStateEnum.DELETED) [
		throw new IllegalStateException('''unexpected source delete''')
	].addLifeCycle(Lifecycles.getDefault(true, true)).build

	val targetRule = createRule.precondition(TargetMatcher.querySpecification).action [
		handler.targetMatch(parent, entry, correspondence)
	].action(CRUDActivationStateEnum.UPDATED) [
		throw new IllegalStateException('''unexpected target update''')
	].action(CRUDActivationStateEnum.DELETED) [
		throw new IllegalStateException('''unexpected source delete''')
	].addLifeCycle(Lifecycles.getDefault(true, true)).build

	def dispose() {
		if (transformation != null) {
			transformation.dispose
		}
		transformation = null
		return
	}
}
