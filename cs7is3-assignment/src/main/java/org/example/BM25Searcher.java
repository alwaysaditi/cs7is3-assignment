package org.example;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryparser.classic.MultiFieldQueryParser;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.similarities.BM25Similarity;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;

public class BM25Searcher {
    public static void search(String query, int index) throws IOException {
        String filePath = "cs7is3-assignment-main/cs7is3-assignment/output/bm25_similarity.txt";
        File outputFile = new File(filePath);
        if (outputFile.exists() && index ==0) {
            // Delete the file
            outputFile.delete();
            System.out.println("Existing file deleted.");
            return;
        }

        // Create the output file (this will create the file if it doesn't exist)
        outputFile.createNewFile();
        System.out.println("New file created.");

        if(index == 0) return;
        String indexPath = "new_index";  // Path to your Lucene index
        //String fieldName = "content"; // The field you indexed your documents on
        Analyzer analyzer = LuceneIndexer.analyzer; // Your custom analyzer

        try {
            // Open the index
            Directory dir = FSDirectory.open(Paths.get(indexPath));
            IndexReader reader = DirectoryReader.open(dir);
            IndexSearcher searcher = new IndexSearcher(reader);
            System.out.println(query);

            // If BM25 is the default similarity, no need to set it explicitly
            searcher.setSimilarity(new BM25Similarity());

            // Loop through the 225 preprocessed queries

                // Parse the query string using the analyzer
            MultiFieldQueryParser parser = new MultiFieldQueryParser(new String[] {"title","author","bibliography","content"}, analyzer);
                Query queryFinal = parser.parse(QueryParser.escape(query));

                // Execute the search (adjust the number of top documents as necessary)
                TopDocs topDocs = searcher.search(queryFinal, 50); // Retrieves top 10 documents

            StringBuilder output = new StringBuilder();


            // Building the content to append
//        output.append("ALL SCORES for QUERY: ").append(queryString)
//                .append(" with query number: ").append(qno)
            output .append(" actual query number: ").append(index)
                    .append("\n");

            // Append score entries (up to 50)

                // Process search results
                System.out.println("Results for query number: " + index);
                for (ScoreDoc scoreDoc : topDocs.scoreDocs) {
                    Document doc = searcher.doc(scoreDoc.doc);
                    output.append("DocID: " + scoreDoc.doc + " Score: " + scoreDoc.score);

                }


            // Close reader
            reader.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
