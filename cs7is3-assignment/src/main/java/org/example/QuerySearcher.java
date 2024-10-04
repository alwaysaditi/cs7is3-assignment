package org.example;

import org.apache.commons.math3.linear.RealVector;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.*;

import static org.example.IndexViewer.*;

public class QuerySearcher {
    // int[] numbers = new int[1401]; // Create an array of size 1401

    public static void searchQuery(String queryString)
    {

        Map<Integer, Double> topFiveDocs = new HashMap<>();

        List<Map.Entry<Integer, Double>> scoreList = new ArrayList<>();

        for (int i = 0; i <= 1399; i++) {
            termsglobal.clear();
            String indexPath = "new_index"; // Path to your index directory
            int docId = i; // Replace with the actual document ID you want to retrieve
            Map<String, Integer> queryFrequencies = getQueryFrequencies(queryString);
            RealVector v1  ;
            RealVector v2 ;
            try {
                // Open the index directory
                Directory dir = FSDirectory.open(Paths.get(indexPath));
                IndexReader reader = DirectoryReader.open(dir);

                // Retrieve term frequencies for the specified document ID
                Map<String, Integer> termFrequencies = getTermFrequencies(reader, docId);
                //System.out.println("Term Frequencies for Document " + docId + ": " + termFrequencies);
                // System.out.println("Query Frequencies: " + queryFrequencies);

              
                v1 = toRealVector(queryFrequencies);
                v2 = toRealVector(termFrequencies);

                // Calculate the cosine similarity score
                double cosineSimilarityScore = getCosineSimilarity(v1, v2); 
                // System.out.println("Term Frequencies for Document " +docId+1 + ": " + termFrequencies);
                // System.out.println("Query Frequencies: " + queryFrequencies);

                 //Print the global terms set
                // System.out.println("Terms Global HashSet: " + termsglobal);
                // System.out.println("size ofmap1= "+termFrequencies.size()+" size of map2 = "+queryFrequencies.size()+" size of hashset = "+termsglobal.size());

                                Set<String> commonKeys = new HashSet<>(termFrequencies.keySet());  // Make a copy of keys1
                commonKeys.retainAll(queryFrequencies.keySet());  // Retains only the common elements with keys2

                // Print the common keys
              //  System.out.println("Common keys: " + commonKeys);
                // Store the score and document ID in the list
                scoreList.add(new AbstractMap.SimpleEntry<>(docId+1, cosineSimilarityScore));
//System.out.println("size ofmap1= "+termFrequencies.size()+" size of map2 = "+queryFrequencies.size()+" size of hashset = "+termsglobal.size());
                reader.close(); // Close the index reader
            } catch (IOException e) {
                System.out.println("Caught an " + e.getClass() + " with message: " + e.getMessage());
            }
        }
        scoreList.sort((a, b) -> Double.compare(b.getValue(), a.getValue()));
        // Get the top five scores and their document IDs
        for (int j = 0; j < Math.min(5, scoreList.size()); j++) {
            Map.Entry<Integer, Double> entry = scoreList.get(j);
            topFiveDocs.put(entry.getKey(), entry.getValue());
        }

        // Print the top five documents and their scores
        System.out.println("ALL SCORES for QUERY: "+queryString);
        for(int i=0; i<50;i++)
        {
            System.out.println(scoreList.get(i).getKey()+": "+scoreList.get(i).getValue());
        }
    }

    static double getCosineSimilarity(RealVector v1, RealVector v2) {
        return (v1.dotProduct(v2)) / (v1.getNorm() * v2.getNorm());
    }

    public static Map<String,Integer> getQueryFrequencies(String query)
    {
        Map<String, Integer> frequencies = new HashMap<>();

     
        String[] splitTerms = query.split("\\s+"); // Split by whitespace

        for (String term : splitTerms) {
            term = term.toLowerCase(); // Normalize to lowercase (optional)

            // Update term frequency in the map
            frequencies.put(term, frequencies.getOrDefault(term, 0) + 1);

            // Add the term to the HashSet
            termsglobal.add(term);
        }

        return frequencies; // Return the map of term frequencies
    }
}
