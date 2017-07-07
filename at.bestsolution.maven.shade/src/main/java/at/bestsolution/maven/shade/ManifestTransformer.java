package at.bestsolution.maven.shade;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.jar.Attributes;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.JarOutputStream;
import java.util.jar.Manifest;

import org.apache.maven.plugins.shade.relocation.Relocator;
import org.apache.maven.plugins.shade.resource.ResourceTransformer;

public class ManifestTransformer implements ResourceTransformer {
	private Manifest m;
	
    private String mainClass;
    private Map<String, Object> manifestEntries;
    private Map<String,String> concatEntries;
	
	public boolean canTransformResource(String resource) {
		return JarFile.MANIFEST_NAME.equalsIgnoreCase( resource );
	}
	
	public void processResource(String resource, InputStream is, List<Relocator> relocators) throws IOException {
		if( m == null ) {
			m = new Manifest();
			if ( mainClass != null ) {
				m.getMainAttributes().put( Attributes.Name.MAIN_CLASS, mainClass );
	        }
			
			 if ( manifestEntries != null ) {
				 manifestEntries.entrySet()
				 	.forEach( e -> m.getMainAttributes().put(new Attributes.Name(e.getKey()), e.getValue()) );
		        }
			
			m.getMainAttributes().put(Attributes.Name.MANIFEST_VERSION, "1.0");
			m.getMainAttributes().put(new Attributes.Name("Created-By"), System.getProperty("java.version") + " ("+System.getProperty("java.vendor")+")");
			m.getMainAttributes().put(new Attributes.Name("Tool"), "e(fx)clipse shade transformer");
		}
		
		
		if( concatEntries != null ) {
			Manifest mm = new Manifest(is);
			Attributes attributes = mm.getMainAttributes();
			
			for( Entry<String, String> entry : concatEntries.entrySet() ) {
				String serviceComponents = attributes.getValue(new Attributes.Name(entry.getKey()));
				if( serviceComponents != null ) {
					String value = m.getMainAttributes().getValue(new Attributes.Name(entry.getKey()));
					if( value == null ) {
						value = serviceComponents;
					} else {
						value += entry.getValue() + serviceComponents;
					}
					m.getMainAttributes().put(new Attributes.Name(entry.getKey()), value);
				}
			}
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
