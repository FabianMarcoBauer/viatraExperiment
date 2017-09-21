package experimental.complete;

import experimentalCorrespondence.Correspondence;
import experimentalCorrespondence.ExperimentalCorrespondenceFactory;
import experimentalCorrespondence.TransformationModel;
import experimentalSource.SourceEntry;
import experimentalTarget.TargetEntry;

public class TransformationHandler {
	private TransformationModel model;
	

	public TransformationHandler(TransformationModel model) {
		this.model = model;
	}
	
	public void completeMatch(SourceEntry sourceParent, SourceEntry sourceEntry, TargetEntry targetParent, TargetEntry targetEntry, Correspondence correspondence, int identifier) {
		connect(sourceEntry, targetEntry);
	}
	
	private void connect(SourceEntry source, TargetEntry target) {
		Correspondence correspondence = ExperimentalCorrespondenceFactory.eINSTANCE.createCorrespondence();
		correspondence.setSource(source);
		correspondence.setTarget(target);
		model.getCorr().add(correspondence);
	}
	
}
