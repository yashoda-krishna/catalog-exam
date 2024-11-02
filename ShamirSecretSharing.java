package Prac;

import org.json.JSONObject;
import org.json.JSONTokener;
import java.io.FileReader;
import java.io.IOException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

public class ShamirSecretSharing {

    static class Root {
        int x;
        BigInteger y;

        Root(int x, BigInteger y) {
            this.x = x;
            this.y = y;
        }
    }

    public static void main(String[] args) {
        // Define the file paths for both input files
        String[] filePaths = { "input1.json", "input2.json" };
        
        // Process each file
        for (String filePath : filePaths) {
            processJsonFile(filePath);
        }
    }

    private static void processJsonFile(String filePath) {
        try (FileReader reader = new FileReader(filePath)) {
            JSONObject jsonObject = new JSONObject(new JSONTokener(reader));

            // Get k and n values
            int n = jsonObject.getJSONObject("keys").getInt("n");
            int k = jsonObject.getJSONObject("keys").getInt("k");

            // Extract roots and decode y values
            List<Root> roots = new ArrayList<>();
            for (int i = 1; i <= n; i++) {
                if (jsonObject.has(String.valueOf(i))) {
                    JSONObject rootJson = jsonObject.getJSONObject(String.valueOf(i));
                    int base = rootJson.getInt("base");
                    String valueStr = rootJson.getString("value");
                    int x = i;
                    BigInteger y = new BigInteger(valueStr, base);  // Decodes y value to decimal
                    roots.add(new Root(x, y));
                }
            }

            // Find the constant term using Lagrange interpolation
            BigInteger secret = findConstantTerm(roots, k);
            System.out.println("Secret constant term from " + filePath + ": " + secret);

        } catch (IOException e) {
            System.err.println("Error reading file: " + filePath);
            e.printStackTrace();
        }
    }

    // Method for Lagrange interpolation to find the constant term
    private static BigInteger findConstantTerm(List<Root> roots, int k) {
        BigInteger result = BigInteger.ZERO;

        for (int i = 0; i < k; i++) {
            Root r_i = roots.get(i);
            BigInteger term = r_i.y;

            for (int j = 0; j < k; j++) {
                if (i != j) {
                    Root r_j = roots.get(j);
                    BigInteger numerator = BigInteger.valueOf(-r_j.x);
                    BigInteger denominator = BigInteger.valueOf(r_i.x - r_j.x);
                    term = term.multiply(numerator).divide(denominator);
                }
            }
            result = result.add(term);
        }

        return result;
    }
}

