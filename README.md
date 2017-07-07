# Maven Shade Extensions

Project with a set of extensions for the [Maven Shade Plugin](http://maven.apache.org/plugins/maven-shade-plugin/)

## ManifestTransformer

Similar to the basic ManifestTransformer it allows you transform the MANIFEST.MF for the shaded jar but it allows you 
to collect entries from the embedded MANIFEST.MF

### Usage

```xml
<build>
		<plugins>
			<plugin>
				<groupId>org.apache.maven.plugins</groupId>
				<artifactId>maven-shade-plugin</artifactId>
				<version>3.0.0</version>
				<dependencies>
					<dependency>
						<groupId>at.bestsolution</groupId>
						<artifactId>at.bestsolution.maven.shade</artifactId>
						<version>1.0.0</version>
					</dependency>
				</dependencies>
				...
				<configuration>
					...
					<transformers>
						<transformer
							implementation="at.bestsolution.maven.shade.ManifestTransformer">
							<mainClass>test.services.c.App</mainClass>
							<manifestEntries>
								<Implementation-Vendor>BestSolution.at</Implementation-Vendor>  
							</manifestEntries>
							<concatEntries>
								<Service-Component>,</Service-Component>
							</concatEntries>
						</transformer>
					</transformers>
				</configuration>
			</plugin>
		</plugins>
	</build>
```
