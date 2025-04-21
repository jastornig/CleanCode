# CleanCode WebCrawler

## Usage

Beneath the folder `resources` a file with the name `RunWebCrawler.xml` can be found, which contains a Run Configuration, that can be imported into Intellij.

Another way to run the WebCrawler is to use the following maven command:

```bash
mvn clean install -Dexec.mainClass=paulxyh.WebCrawler -Dexec.args="<url> <depth> <domains>"
```

* `url` => The root url which the Crawler should target 
* `depth` => The maximal depth the Crawler should go
* `domains` => A comma seperated list of all possible domains

## Testing

Run the following maven command to run all Unit- and IntegrationTests:

```bash
mvn test
```