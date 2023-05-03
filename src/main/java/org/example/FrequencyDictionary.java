package org.example;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;
import static java.lang.Character.UnicodeBlock.CYRILLIC;
import static java.lang.Character.UnicodeBlock.of;

public class FrequencyDictionary {
    private final Map<String, Integer> words = new HashMap<>();

    public void read() throws FileNotFoundException {
        System.out.println("Введите имя файла для проведения анализа:");
        Scanner console = new Scanner(System.in);
        String fileName = console.nextLine();
        try (BufferedReader br = new BufferedReader(new FileReader(fileName, StandardCharsets.UTF_8))) {
            String x;
            while ((x = br.readLine()) != null) {
                x = x.toLowerCase();
                int z = 0;
                while (z < x.length()) {
                    while (z < x.length() && of(x.charAt(z)) != CYRILLIC)
                        z++;
                    int st = z;
                    while (z < x.length() && (of(x.charAt(z)) == CYRILLIC || x.charAt(z) == '-'))
                        z++;
                    if (st < z) {
                        String w = x.substring(st, z);
                        if (words.containsKey(w)) {
                            words.put(w, words.get(w) + 1);
                        } else {
                            words.put(w, 1);
                        }
                    }
                }
            }
        } catch (FileNotFoundException e) {
            System.out.println("Не удается найти указанный файл, убедитесь что имя файла указано верно.");
            read();
        } catch (IOException e) {
            System.out.println("---------------------------IOException e-------------------------------");
        }
        reports();
    }

    public void reports() {
        Map.Entry<String, Integer>[] data = words.entrySet().toArray(new Map.Entry[0]);
        int totalWords = 0;
        for (Map.Entry<String, Integer> e : data) {
            totalWords += e.getValue();
        }
        Arrays.sort(data, Map.Entry.comparingByKey());
        allReports("report-by-alph.txt", data, totalWords);

        Arrays.sort(data, (e1, e2) -> reverseDictionary(e1.getKey(), e2.getKey()));
        allReports("report-by-alph-rev.txt", data, totalWords);
        Arrays.sort(data, (e1, e2) -> {
            int r = -e1.getValue().compareTo(e2.getValue());
            if (r != 0)
                return r;
            return e1.getKey().compareTo(e2.getKey());
        });
        allReports("report-by-freq.txt", data, totalWords);
    }


    private void allReports(String fileName, Map.Entry<String, Integer>[] data, int totalWords) {
        try (OutputStreamWriter writer = new OutputStreamWriter(new FileOutputStream(fileName), StandardCharsets.UTF_8)){
            for (Map.Entry<String, Integer> e : data) {
                String ee;
                ee =  e.getKey();
                writer.write(ee + " - абсолютная частота(встречается в тексте раз): " + e.getValue() + ", относительная частота: "
                        + ((float) e.getValue() / totalWords) + "\n");
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static int reverseDictionary(String s1, String s2) {
        int p1 = s1.length() - 1;
        int p2 = s2.length() - 1;
        while (p1 >= 0 && p2 >= 0) {
            int r = Character.compare(s1.charAt(p1), s2.charAt(p2));
            if (r != 0)
                return r;
            p1--;
            p2--;
        }
        return p1 - p2;
    }
}
