# ChocoCommons
A set of common utilities for Choco's public Bukkit/Spigot plugins. Used currently in the following plugins:
- Alchema @ https://github.com/2008Choco/Alchema/
- AlchemicalArrows @ https://github.com/2008Choco/AlchemicalArrows/
- DragonEggDrop @ https://github.com/2008Choco/DragonEggDrop/
- LockSecurity @ https://github.com/2008Choco/LockSecurity/
- VeinMiner @ https://github.com/2008Choco/VeinMiner/

# Depending on ChocoCommons
No releases are made for ChocoCommons. Instead, developers are encouraged to make use of Jitpack and its commit-based version dependencies. Additionally, the Maven Shade Plugin or Gradle Shadow Plugin should be used to include the ChocoCommons classes inside of the final compiled jar. This is a set of libraries, not a plugin, and will not be available at runtime unless shaded.

## Maven
```xml
<project>
  ...
  <build>
  <plugins>
    <plugin>
      <groupId>org.apache.maven.plugins</groupId>
      <artifactId>maven-shade-plugin</artifactId>
      <version>3.2.1</version>
      <configuration>
        <minimizeJar>true</minimizeJar>
        <relocations>
          <relocation>
            <pattern>wtf.choco.commons</pattern>
            <shadedPattern>your.project.package</shadedPattern>
          </relocation>
        </relocations>
      </configuration>
      <executions>
        <execution>
          <phase>package</phase>
          <goals>
            <goal>shade</goal>
          </goals>
        </execution>
      </executions>
    </plugin>
  </build>
  ...
  <repositories>
    <id>jitpack</id>
    <url>https://jitpack.io/</url>
  </repositories>
  ...
  <dependencies>
    <dependency>
      <groupId>com.github.2008Choco</groupId>
      <artifactId>ChocoCommons</artifactId>
      <version>SHORT-COMMIT-HASH-HERE</version>
      <scope>compile</scope>
    </dependency>
  </dependencies>
  ...
</project>
```

## Gradle
```groovy
  plugins {
      id 'com.github.johnrengelman.shadow' version '6.1.0'
  }

  repositories {
    jcenter()
    maven { url "https://jitpack.io" }
  }

  dependencies {
    compile 'com.github.2008Choco:ChocoCommons:SHORT-COMMIT-HASH-HERE'
  }

  shadowJar {
    relocate 'wtf.choco.commons', 'your.project.package'
  }
```
