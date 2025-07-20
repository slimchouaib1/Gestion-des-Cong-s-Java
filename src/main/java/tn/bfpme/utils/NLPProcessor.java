package tn.bfpme.utils;

import edu.stanford.nlp.pipeline.*;

import java.util.Properties;

public class NLPProcessor {
    private StanfordCoreNLP pipeline;

    public NLPProcessor() {
        Properties props = new Properties();
        props.setProperty("annotators", "tokenize,ssplit,pos,lemma,ner,parse");
        pipeline = new StanfordCoreNLP(props);
    }

    public String process(String text) {
        CoreDocument document = new CoreDocument(text);
        pipeline.annotate(document);

        // For now, just return the text for demonstration purposes
        return document.text();
    }
}
