InvertedIndex Builder and SearchEngine
--------------------------------------

To run using SBT:
> cd InvertedIndexAndSearch
> sbt "run-main engine.Boot /directoryContainingTextFiles"

To run tests using SBT:
> sbt test
- NOTE: If running test IndexBuilderSpec from Intellij directly, ensure that 'sbt test' was run prior so that resource folders are added to target/test-classes 
