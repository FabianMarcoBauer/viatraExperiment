package experimental.partial;

import java.io.File;
import java.io.IOException;
import java.util.Random;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.xmi.impl.EcoreResourceFactoryImpl;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceFactoryImpl;
import org.eclipse.viatra.query.runtime.api.ViatraQueryEngine;
import org.eclipse.viatra.query.runtime.emf.EMFScope;
import org.eclipse.viatra.query.runtime.exception.ViatraQueryException;

import experimentalCorrespondence.Correspondence;
import experimentalCorrespondence.ExperimentalCorrespondenceFactory;
import experimentalCorrespondence.ExperimentalCorrespondencePackage;
import experimentalCorrespondence.TransformationModel;
import experimentalSource.ExperimentalSourcePackage;
import experimentalSource.SourceEntry;
import experimentalTarget.ExperimentalTargetPackage;
import experimentalTarget.TargetEntry;

public class CCRunner {

	static {
		// register extensions
		Resource.Factory.Registry.INSTANCE.getExtensionToFactoryMap().put("ecore", new EcoreResourceFactoryImpl());
		Resource.Factory.Registry.INSTANCE.getExtensionToFactoryMap().put("xmi", new XMIResourceFactoryImpl());

		// initialize packages
		@SuppressWarnings("unused")
		ExperimentalCorrespondencePackage trafoModel = ExperimentalCorrespondencePackage.eINSTANCE;
		@SuppressWarnings("unused")
		ExperimentalSourcePackage lp = ExperimentalSourcePackage.eINSTANCE;
		@SuppressWarnings("unused")
		ExperimentalTargetPackage dp = ExperimentalTargetPackage.eINSTANCE;
	}

	public static void main(String[] args) throws ViatraQueryException, IOException {
		System.out.println("partial");
		File f = new File("instances");
		if (!f.exists()) {
			f.mkdir();
		}
		ResourceSet rs = new ResourceSetImpl();
		Resource sourceResource;
		Resource targetResource;
		Resource corrResource;
		
		TransformationModel transformationModel;
		
		if (args.length > 0 && "l".equals(args[0])) {
			transformationModel = ExperimentalCorrespondenceFactory.eINSTANCE.createTransformationModel();
			
			sourceResource = rs
					.getResource(URI.createFileURI(new File("instances/src.xmi").getAbsolutePath()), true);
			transformationModel.setSource((SourceEntry) sourceResource.getContents().get(0));
			targetResource = rs
					.getResource(URI.createFileURI(new File("instances/trg.xmi").getAbsolutePath()), true);
			transformationModel.setTarget((TargetEntry) targetResource.getContents().get(0));
			corrResource = rs
					.createResource(URI.createFileURI(new File("instances/corr.xmi").getAbsolutePath()));
			corrResource.getContents().add(transformationModel);

			Correspondence correspondence = ExperimentalCorrespondenceFactory.eINSTANCE.createCorrespondence();
			correspondence.setSource(transformationModel.getSource());
			correspondence.setTarget(transformationModel.getTarget());
			transformationModel.getCorr().add(correspondence);
		} else {
			// Generator generator = new Generator(new Random(), 0, 0, 0, 0, 0,
			// 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1);
			GeneratorSettings s = new GeneratorSettings(0, 0);
			s.addCommonSize(1, 1_000_000);
//			s.addCommonSize(10, 1);
//			s.addCommonSize(100, 1);
			Generator generator = new Generator(new Random(0), s);

			transformationModel = generator.getTransformationModel();

			sourceResource = rs
					.createResource(URI.createFileURI(new File("instances/src.xmi").getAbsolutePath()));
			sourceResource.getContents().add(transformationModel.getSource());
			targetResource = rs
					.createResource(URI.createFileURI(new File("instances/trg.xmi").getAbsolutePath()));
			targetResource.getContents().add(transformationModel.getTarget());
			corrResource = rs
					.createResource(URI.createFileURI(new File("instances/corr.xmi").getAbsolutePath()));
			corrResource.getContents().add(transformationModel);
		}

		ViatraQueryEngine engine = ViatraQueryEngine.on(new EMFScope(rs));
		Transformation transformation = new Transformation(transformationModel, engine);

		long start = System.nanoTime();
		transformation.execute();
		long end = System.nanoTime();
		System.out.format("transformation completed in %.9fs\r\n", (end - start) / 1_000_000_000d);
//		if (!(args.length > 0 && "l".equals(args[0]))) {
//			sourceResource.save(null);
//			targetResource.save(null);
//		}
//		corrResource.save(null);
		System.out.println(transformationModel.getCorr().size());
	}

}
