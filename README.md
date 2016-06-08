# Linked CrunchBase

A Linked Data wrapper for [CrunchBase](http://www.crunchbase.com/), a database about technology companies anybody can edit.

Compared to the orginal API, our Wrapper converts the given JSON to [JSON-LD](http://json-ld.org/) or [N-Triples](https://www.w3.org/2001/sw/RDFCore/ntriples/). It also enriches the output with links to [DBpedia](http://wiki.dbpedia.org/).

An online demo can be found [here](http://km.aifb.kit.edu/services/crunchbase/). However, you will need an API key from CrunchBase to use it.
To deploy it on your own Server, you can either build it from the sources in /wrapper

/kb-data contains our ontology, our mapping to DBpedia, the vocabulary, the voaf-file and the void-file.