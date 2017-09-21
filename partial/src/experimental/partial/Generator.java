package experimental.partial;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.xmi.impl.EcoreResourceFactoryImpl;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceFactoryImpl;

import experimentalCorrespondence.Correspondence;
import experimentalCorrespondence.ExperimentalCorrespondenceFactory;
import experimentalCorrespondence.ExperimentalCorrespondencePackage;
import experimentalCorrespondence.TransformationModel;
import experimentalSource.ExperimentalSourceFactory;
import experimentalSource.ExperimentalSourcePackage;
import experimentalSource.SourceEntry;
import experimentalTarget.ExperimentalTargetFactory;
import experimentalTarget.ExperimentalTargetPackage;
import experimentalTarget.TargetEntry;

public class Generator {
	private static final int ID_RANGE = 3_000_000;
	private static final int COMMON_ID_OFFSET = 1_000_000;
	private static final int SOURCE_ID_OFFSET = COMMON_ID_OFFSET + ID_RANGE;
	private static final int TARGET_ID_OFFSET = SOURCE_ID_OFFSET + ID_RANGE;

	static {
		// register extensions
		Resource.Factory.Registry.INSTANCE.getExtensionToFactoryMap().put("ecore", new EcoreResourceFactoryImpl());
		Resource.Factory.Registry.INSTANCE.getExtensionToFactoryMap().put("llbtodict", new XMIResourceFactoryImpl());
		Resource.Factory.Registry.INSTANCE.getExtensionToFactoryMap().put("llb", new XMIResourceFactoryImpl());
		Resource.Factory.Registry.INSTANCE.getExtensionToFactoryMap().put("dictionary", new XMIResourceFactoryImpl());

		// initialize packages
		@SuppressWarnings("unused")
		ExperimentalCorrespondencePackage trafoModel = ExperimentalCorrespondencePackage.eINSTANCE;
		@SuppressWarnings("unused")
		ExperimentalSourcePackage lp = ExperimentalSourcePackage.eINSTANCE;
		@SuppressWarnings("unused")
		ExperimentalTargetPackage dp = ExperimentalTargetPackage.eINSTANCE;
	}

	private TransformationModel transformationModel;
	private List<SourceEntry> sourceEntries;
	private List<TargetEntry> targetEntries;
	private Random r;

	@Deprecated
	public Generator(Random r, int sourceOnly, int targetOnly, int... commonMultiples) {
		this.r = r;
		sourceEntries = new ArrayList<>();
		targetEntries = new ArrayList<>();

		transformationModel = ExperimentalCorrespondenceFactory.eINSTANCE.createTransformationModel();
		SourceEntry sourceRoot = ExperimentalSourceFactory.eINSTANCE.createSourceEntry();
		sourceRoot.setIdentifier(1);
		sourceEntries.add(sourceRoot);
		transformationModel.setSource(sourceRoot);
		TargetEntry targetRoot = ExperimentalTargetFactory.eINSTANCE.createTargetEntry();
		targetRoot.setIdentifier(1);
		targetEntries.add(targetRoot);
		transformationModel.setTarget(targetRoot);

		Correspondence correspondence = ExperimentalCorrespondenceFactory.eINSTANCE.createCorrespondence();
		correspondence.setSource(sourceRoot);
		correspondence.setTarget(targetRoot);
		transformationModel.getCorr().add(correspondence);
		System.out.println(transformationModel.getCorr());

		for (int i = 0; i < commonMultiples.length; i++) {
			createCommonNodes(i + 1, commonMultiples[i]);
		}
		createSourceNodes(sourceOnly);
		createTargetNodes(targetOnly);
	}

	public Generator(Random r, GeneratorSettings s) {
		this.r = r;
		sourceEntries = new ArrayList<>();
		targetEntries = new ArrayList<>();

		transformationModel = ExperimentalCorrespondenceFactory.eINSTANCE.createTransformationModel();
		SourceEntry sourceRoot = ExperimentalSourceFactory.eINSTANCE.createSourceEntry();
		sourceRoot.setIdentifier(1);
		sourceEntries.add(sourceRoot);
		transformationModel.setSource(sourceRoot);
		TargetEntry targetRoot = ExperimentalTargetFactory.eINSTANCE.createTargetEntry();
		targetRoot.setIdentifier(1);
		targetEntries.add(targetRoot);
		transformationModel.setTarget(targetRoot);

		Correspondence correspondence = ExperimentalCorrespondenceFactory.eINSTANCE.createCorrespondence();
		correspondence.setSource(sourceRoot);
		correspondence.setTarget(targetRoot);
		transformationModel.getCorr().add(correspondence);
		System.out.println(transformationModel.getCorr());

		// for (int i=0; i<commonMultiples.length; i++) {
		// createCommonNodes(r, i+1, commonMultiples[i], sourceEntries,
		// targetEntries);
		// }
		s.forEachCommonSize(this::createCommonNodes);
		createSourceNodes(s.getSourceOnly());
		createTargetNodes(s.getTargetOnly());

		System.out.println("sourceSize: " + sourceEntries.size());
		System.out.println("targetSize: " + targetEntries.size());
	}

	public TransformationModel getTransformationModel() {
		return transformationModel;
	}

	private void createTargetNodes(int amount) {
		for (int i = 0; i < amount; i++) {
			int identifier = r.nextInt(ID_RANGE) + TARGET_ID_OFFSET;
			TargetEntry targetEntry = ExperimentalTargetFactory.eINSTANCE.createTargetEntry();
			targetEntry.setIdentifier(identifier);

			int index = r.nextInt(targetEntries.size());
			TargetEntry targetParent = targetEntries.get(index);
			targetParent.getChildren().add(targetEntry);

			targetEntries.add(targetEntry);
		}
	}

	private void createSourceNodes(int amount) {
		for (int i = 0; i < amount; i++) {
			int identifier = r.nextInt(ID_RANGE) + SOURCE_ID_OFFSET;
			SourceEntry sourceEntry = ExperimentalSourceFactory.eINSTANCE.createSourceEntry();
			sourceEntry.setIdentifier(identifier);

			int index = r.nextInt(sourceEntries.size());
			SourceEntry sourceParent = sourceEntries.get(index);
			sourceParent.getChildren().add(sourceEntry);

			sourceEntries.add(sourceEntry);
		}
	}

	private void createCommonNodes(int groupSize, int groupCount) {
		for (int i = 0; i < groupCount; i++) {
			int identifier = r.nextInt(ID_RANGE) + COMMON_ID_OFFSET;
			for (int j = 0; j < groupSize; j++) {
				SourceEntry sourceEntry = ExperimentalSourceFactory.eINSTANCE.createSourceEntry();
				sourceEntry.setIdentifier(identifier);
				TargetEntry targetEntry = ExperimentalTargetFactory.eINSTANCE.createTargetEntry();
				targetEntry.setIdentifier(identifier);

				int index = r.nextInt(sourceEntries.size());
				SourceEntry sourceParent = sourceEntries.get(index);
				sourceParent.getChildren().add(sourceEntry);
				TargetEntry targetParent = targetEntries.get(index);
				targetParent.getChildren().add(targetEntry);

				sourceEntries.add(sourceEntry);
				targetEntries.add(targetEntry);
			}
		}
	}
}
