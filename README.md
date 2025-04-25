# CleanCode WebCrawler

## Usage
**All working runConfigs are provided as .xml-files inside the `resources`-folder and can be easily imported into Intellij**

Beneath the folder `resources` a file with the name `RunWebCrawler.xml` can be found, which contains a Run Configuration, that can be imported into Intellij.

Another way to run the WebCrawler is to run the following maven command manually inside the root directory:

```bash
mvn clean install -Dexec.mainClass=paulxyh.WebCrawler -Dexec.args="<url> <depth> <domains>"
```

* `url` => The root url which the Crawler should target
* `depth` => The maximal depth the Crawler should go
* `domains` => A comma seperated list of all possible domains

**I included the maven plugin `exec` in my build process so please do not run `mvn clean install exec:java ...`, otherwise the crawling will be executed twice, which should be fine, as the file is just overwritten, but will use extra processing time**

## Testing

### Visibility and Testing Disclaimer

I know that the usage of Reflection is not really optimal, but for the purpose of this project I will use it, so I can still have the desired visibility levels in my classes.

### Execution of the tests

Run the following maven command to run all Unit- and IntegrationTests:

```bash
mvn clean test
```