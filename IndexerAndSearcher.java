package com.example.auhorizationserver;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.*;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import java.nio.file.Paths;


public class IndexerAndSearcher {
    public static void main(String[] args) throws IOException, ParseException {
        String indexDir = "./src/main/java/resources/indexDir";
        String dataDir = "./src/main/java/resources/dataDir";
        StandardAnalyzer standardAnalyzer=new StandardAnalyzer();
        IndexWriterConfig conf = new IndexWriterConfig(standardAnalyzer);
        Directory dir = FSDirectory.open(new File(indexDir).toPath());
        System.out.println(dir);
        IndexWriter indexWriter=new IndexWriter(dir,conf);

        File[] files = new File(dataDir).listFiles();

        if (files != null) {
            for (File f : files) {
                if (!f.isDirectory() &&
                        !f.isHidden() &&
                        f.exists() &&
                        f.canRead()
                ) {
                    //f raw file
                    System.out.println(f.getAbsolutePath());
                    Document document = new Document();
                    document.add(new TextField("contents",new FileReader(f)));
                    document.add(new TextField("title", f.getAbsolutePath(), Field.Store.YES));
                    System.out.println( indexWriter.addDocument(document));
                    indexWriter.close();

                }
            }
        }

        //=========================================
        // searching                Takes in index directory--> Creates Query and Executes
        //=========================================
        Directory dir2 = FSDirectory.open(Paths.get(indexDir));
        IndexReader reader = DirectoryReader.open(dir);

        IndexSearcher is = new IndexSearcher(reader);

        QueryParser parser = new QueryParser("contents",
                new StandardAnalyzer());
        Query query = parser.parse("mail");

        TopDocs hits = is.search(query, 10);
        System.out.println("total hits"+hits.totalHits);

        for(ScoreDoc scoreDoc : hits.scoreDocs) {
            Document doc = is.doc(scoreDoc.doc);

            System.out.println(doc.get("title"));
        }

    }
}
