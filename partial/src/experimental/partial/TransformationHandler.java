package experimental.partial;

import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

import experimentalCorrespondence.Correspondence;
import experimentalCorrespondence.ExperimentalCorrespondenceFactory;
import experimentalSource.SourceEntry;
import experimentalTarget.TargetEntry;
import experimentalCorrespondence.TransformationModel;

public class TransformationHandler {
	// Matches werden theoretisch nicht benötigt. Es könnten direkt die Source- oder TargetEntries gespeichert werden
	private Map<Key, Stack<SourceMatchEntry>> sourceMatches;
	private Map<Key, Stack<TargetMatchEntry>> targetMatches;
	private TransformationModel model;
	

	public TransformationHandler(TransformationModel model) {
		this.model = model;
		sourceMatches = new HashMap<>();
		targetMatches = new HashMap<>();
	}
	
	public void sourceMatch(SourceEntry parent, SourceEntry entry, Correspondence correspondence) {
		SourceMatchEntry sme = new SourceMatchEntry(parent, entry, correspondence);
		Key key = new Key(correspondence, entry.getIdentifier());
		Stack<TargetMatchEntry> targetMatchStack = targetMatches.get(key);
		if (targetMatchStack!= null && !targetMatchStack.isEmpty()) {
			TargetMatchEntry targetMatchEntry = targetMatchStack.pop();
			connect(sme, targetMatchEntry);
//			System.out.println("matched source entry:      "+entry);
		} else {
			Stack<SourceMatchEntry> computeIfAbsent = sourceMatches.computeIfAbsent(key, k->new Stack<>());
			computeIfAbsent.add(sme);
//			System.out.println("found lonely source entry: "+entry);
		}
	}

	public void targetMatch(TargetEntry parent, TargetEntry entry, Correspondence correspondence) {
		TargetMatchEntry targetMatchEntry = new TargetMatchEntry(parent, entry, correspondence);
		Key key = new Key(correspondence, entry.getIdentifier());
		Stack<SourceMatchEntry> sourceMatchEntries = sourceMatches.get(key);
		if (sourceMatchEntries!= null && !sourceMatchEntries.isEmpty()) {
			SourceMatchEntry sourceMatchEntry = sourceMatchEntries.pop();
			connect(sourceMatchEntry, targetMatchEntry);
//			System.out.println("matched target entry:      "+entry);
		} else {
			Stack<TargetMatchEntry> computeIfAbsent = targetMatches.computeIfAbsent(key, k->new Stack<>());
			computeIfAbsent.add(targetMatchEntry);
//			System.out.println("found lonely target entry: "+entry);
		}
	}
	
	private void connect(SourceMatchEntry source, TargetMatchEntry target) {
		Correspondence correspondence = ExperimentalCorrespondenceFactory.eINSTANCE.createCorrespondence();
		correspondence.setSource(source.entry);
		correspondence.setTarget(target.entry);
		model.getCorr().add(correspondence);
	}

	
	private static class Key {
		Correspondence correspondence;
		int id;
		public Key(Correspondence correspondence, int id) {
			super();
			this.correspondence = correspondence;
			this.id = id;
		}
		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + ((correspondence == null) ? 0 : correspondence.hashCode());
			result = prime * result + id;
			return result;
		}
		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			Key other = (Key) obj;
			if (correspondence == null) {
				if (other.correspondence != null)
					return false;
			} else if (!correspondence.equals(other.correspondence))
				return false;
			if (id != other.id)
				return false;
			return true;
		}
	}
	
	private static class SourceMatchEntry {

		private SourceEntry parent;
		private SourceEntry entry;
		private Correspondence correspondence;

		public SourceMatchEntry(SourceEntry parent, SourceEntry entry, Correspondence correspondence) {
			this.parent = parent;
			this.entry = entry;
			this.correspondence = correspondence;
		}
		
	}
	
	private static class TargetMatchEntry {
		private TargetEntry parent;
		private TargetEntry entry;
		private Correspondence correspondence;

		public TargetMatchEntry(TargetEntry parent, TargetEntry entry, Correspondence correspondence) {
			this.parent = parent;
			this.entry = entry;
			this.correspondence = correspondence;
		}
	}
}
