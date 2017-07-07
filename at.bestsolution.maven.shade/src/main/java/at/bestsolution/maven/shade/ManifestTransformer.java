package at.bestsolution.maven.shade;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.jar.Attributes;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.JarOutputStream;
import java.util.jar.Manifest;

import org.apache.maven.plugins.shade.relocation.Relocator;
import org.apache.maven.plugins.shade.resource.ResourceTransformer;

public class ManifestTransformer implements ResourceTransformer {
	private Manifest m;
	
	// Configuration
    private String mainClass;
	
	public boolean canTransformResource(String resource) {
		return JarFile.MANIFEST_NAME.equalsIgnoreCase( resource );
	}
	
	public void processResource(String resource, InputStream is, List<Relocator> relocators) throws IOException {
		if( m == null ) {
			m = new Manifest();
			if ( mainClass != null ) {
				m.getMainAttributes().put( Attributes.Name.MAIN_CLASS, mainClass );
	        }
			m.getMainAttributes().put(Attributes.Name.MANIFEST_VERSION, "1.0");
			m.getMainAttributes().put(new Attributes.Name("Created-By"), System.getProperty("java.version") + " ("+System.getProperty("java.vendor")+")");
			m.getMainAttributes().put(new Attributes.Name("Tool"), "e(fx)clipse shade transformer");
		}
		
		Manifest mm = new Manifest(is);
		Attributes attributes = mm.getMainAttributes();
		String serviceComponents = attributes.getValue(new Attributes.Name("Service-Component"));
		if( serviceComponents != null ) {
			String value = m.getMainAttributes().getValue(new Attributes.Name("Service-Component"));
			if( value == null ) {
				value = serviceComponents;
			} else {
				value += "," + serviceComponents;
			}
			m.getMainAttributes().put(new Attributes.Name("Service-Component"), value);
		}
	}
	
	public void modifyOutputStream(JarOutputStream jos) throws IOException {
		jos.putNextEntry( new JarEntry( JarFile.MANIFEST_NAME ) );
		m.write(jos);
	}

	public boolean hasTransformedResource() {
		return true;
	}
}
