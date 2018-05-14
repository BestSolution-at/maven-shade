/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package at.bestsolution.maven.shade;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.jar.Attributes;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.JarOutputStream;
import java.util.jar.Manifest;
import java.util.stream.Collectors;

import org.apache.maven.plugins.shade.relocation.Relocator;
import org.apache.maven.plugins.shade.resource.ResourceTransformer;

/**
 * Manifest-Transformer for the Maven Shade Plugin
 */
public class ManifestTransformer implements ResourceTransformer {
	private Manifest m;
	
    private String mainClass;
    private Map<String, Object> manifestEntries;
    private Map<String,String> concatEntries;
    private Set<String> registeredComponents = new HashSet<>();
	
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
					registeredComponents.addAll(Arrays.asList(serviceComponents.split(",")));
					String components = registeredComponents.stream().collect(Collectors.joining(","));
					m.getMainAttributes().put(new Attributes.Name(entry.getKey()), components);
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
