# corenlp-coreference-server
CoreNLP co-reference resolution over http

Based on the [cygnet](https://github.com/soundarmoorthy/cygnet) project.

This project simply acccepts a URLEncoded query string which is a paragraph.
See the <code>src/main/java/devtest/ParagraphTest</code> for an example.  That example
includes a display of what is returned: a JSON Object with two primary fields.

The GPL license was chosen since Stanford's CoreNLP is licensed GPL.

## Building
One dependency is not yet in a maven repo. It must be installed first:

https://github.com/topicquests/tq-support tag: v2.0.0

Then this repo:<br/>
mvn clean install
## Running
./start.sh



