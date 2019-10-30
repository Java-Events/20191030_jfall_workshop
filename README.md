# 2019_OC1_TUT2591
JUnit5: From @Test Up to Your Own TestEngine

## ToDo

Vaadin Testengine bei jUnit5 registrieren
Allgemeine WebTest Engine ?
* Webdriver laden
* Container einmal pro Testdurchlauf hochfahren
  
TDD for TestEngine
* mutationTesting -> Slides wieder verwenden


## From Test to Extensions

## Useless Engine

### IDE and @Testable
Show what happened with an Annotation including @Testable
IDE Support without EngineSupport
Split Annotations on Class- Method-Level

## NanoEngine
* MethodSelector
* ClassSelector
* PackageSelector
* ClasspathRootSelector
 Show how this is done step by step in the IDE and compared to maven::test

## Micro Engine
* Holding Resources, 
* Enabling CDI - Scopes
* forceRandomExecution
    * could be extended with storing random order with MicroStream.one


## MilliEngine
* PageObject creation
* Database replacement
    * testcontainers laden, Modell konvertieren in Microstream -> remote laden
    

Test against JavaFX and Vaadin ?
Wie kann ein TestEngine Framework geschrieben werden?

## XX testEngine
* Distributed Testcontainers 
- https://itnext.io/running-integration-tests-with-kubernetes-ae0fb71e207b


## ClassLoaders

-Djava.system.class.loader=org.rapidpm.junit.engine.distributed.shared.HZClassLoader


## distributed unit test
massiv parallel unit tests
perfect for bigger refactoring session

wie sieht es eigentlich bei MicroStream mit Komprimierung aus?
 Folgendes Beispiel: Wenn man tests aus der 
 DB klassisch füttert, dann ist das Provisionieren 
 und laden halt teuer. 
 Ebenfalls ist das verteilen der Tests deswegen langsam.   
 Wenn man nun aus der DB den Baum einmal holen würde, 
 dann mittels Microstream diesen Graphen einfach speichert, 
 hätte man bei den Tests nicht mehr das elendige Docker 
 was auch immer Problem. Der Mock würde dann diesen 
 Zustand eben laden. Dann kann man das Zeug nutzen 
 ohne schon Produktion umbauen zu müssen.


Im Request wird die JVM mitgegeben, das sorgt dafür das die 
Methode per Testcontainers in der jeweiligen JVM ausgeführt wird.






## Was kann ich mit einer Engine machen?
Die Art wie ein Test beschrieben wird verändern
Resourcen verwalten, DB, ServletContainer, HZ Cluster...
LizenzManagement
Testausführung parallelisieren, async gestallten, ..

Dashboard über Metriken
    - JVM Metriken
    - TestExecution Time
    - LogFiles / Test -> archivieren
    - 

Distributed TestEngine
HZClassLoader läd die Klasse und invoke method
wenn Klasse geladen werden muss


## Ideas
manage resource like ServletContainer
-> TestBench

performance report

RuntimeReport an zentrale Stelle mit infos
JVM/OS , Datum, Zeit, Execution Time
-> Darstellung in VaadinApp

Test gegen JavaFX und Vaadin

FlakyTests




