package sorting;

import java.io.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.stream.Collectors;

class Main {
    public static void main(final String[] args) {
        String inputType = null;
        String sortType = null;
        InputType type = null;
        String inputFileProvided = null;
        String outPutFileProvided = null;
        boolean sortingTypeProvided = false;
        boolean  dontInvokeMethod = false;
        for (int i = 0; i < args.length; i++) {
            if (args[i].equals("-inputFile")) {
                try {
                    if (args[i + 1].matches("[a-z]+\\.[a-z]+")) {
                        inputFileProvided = args[i + 1];
                        File file = new File(inputFileProvided);
                        if (!file.exists()) {
                            file.createNewFile();
                        }
                    }
                } catch (ArrayIndexOutOfBoundsException e) {
                    break;
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
            if (args[i].equals("-outputFile")) {
                try {
                    if (args[i + 1].matches("[a-z]+\\.[a-z]+")) {
                        outPutFileProvided = args[i + 1];
                    }
                    File file = new File(outPutFileProvided);
                    if (!file.exists()) {
                        file.createNewFile();
                    }
                } catch (ArrayIndexOutOfBoundsException e) {
                    break;
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
            if (args[i].equals("-dataType")) {
                try {
                    inputType = args[i + 1].toUpperCase();
                } catch (ArrayIndexOutOfBoundsException e) {
                    dontInvokeMethod = true;
                    System.out.println("No data type defined!");
                    break;
                }
            }
            if (args[i].equals("-sortingType")) {
                try {
                    sortType = args[i + 1];
                    sortingTypeProvided = true;
                } catch (ArrayIndexOutOfBoundsException e) {
                    dontInvokeMethod = true;
                    System.out.println("No sorting type defined!");
                    break;
                }
            }
        }
        if (sortType == null) {
            sortType = "natural";
        }
        try{
            type = InputType.valueOf(inputType);
        } catch (IllegalArgumentException | NullPointerException e) {
            dontInvokeMethod = true;
        }
        if (!dontInvokeMethod) {
            readAndPrintInputs(type, sortType, sortingTypeProvided, inputFileProvided, outPutFileProvided);
        }
    }

    private static void readAndPrintInputs(InputType inputType, String sortType, boolean isSortTypeProvided, String inputFileProvided, String outPutFileProvided) {
        List<String> lineInputs = new ArrayList<>();
        List<Long> longInputs = new ArrayList<>();
        List<String> wordInputs = new ArrayList<>();
        if (inputFileProvided == null) {
            Scanner scanner = new Scanner(System.in);
            switch (inputType) {
                case LINE:
                    while (scanner.hasNextLine()) {
                        lineInputs.add(scanner.nextLine());
                    }
                    break;
                case LONG:
                    while (scanner.hasNext()) {
                        longInputs.add(scanner.nextLong());
                    }
                    break;
                case WORD:
                    while (scanner.hasNext()) {
                        wordInputs.add(scanner.next());
                    }
                    break;
            }
        } else {
            File file = new File(inputFileProvided);
            try (Scanner scanner = new Scanner(file)) {
                switch (inputType) {
                    case LINE:
                        while (scanner.hasNextLine()) {
                            lineInputs.add(scanner.nextLine());
                        }
                        break;
                    case LONG:
                        while (scanner.hasNext()) {
                            longInputs.add(scanner.nextLong());
                        }
                        break;
                    case WORD:
                        while (scanner.hasNext()) {
                            wordInputs.add(scanner.next());
                        }
                        break;
                }
            } catch (IOException e) {
                System.out.println("File not found or cannot be created!");
            }

        }

        Map<String, String> stringIntegerMap = null;
        if (!lineInputs.isEmpty()) {
            stringIntegerMap = sortAndPrint(lineInputs, isSortTypeProvided, sortType, inputType, outPutFileProvided);
        } else if (!longInputs.isEmpty()) {
            stringIntegerMap = sortAndPrint(longInputs, isSortTypeProvided, sortType, inputType, outPutFileProvided);
        } else if (!wordInputs.isEmpty()) {
            stringIntegerMap = sortAndPrint(wordInputs, isSortTypeProvided, sortType, inputType, outPutFileProvided);
        }
    }

    private static <T> Map<String, String> sortAndPrint(List<T> stringInputs, boolean isSortedTypeProvided, String sortType, InputType inputType, String outPutFileProvided) {
        String classStr = stringInputs.get(0).getClass().toString();
        List<Long> longList = null;
        List<String> stringList = null;
        if (classStr.equals("class java.lang.Long")) {
            longList = stringInputs.stream().mapToLong(value -> Long.parseLong(value.toString())).boxed().collect(Collectors.toList());
        } else {
            stringList = stringInputs.stream().map(Objects::toString).collect(Collectors.toList());
        }
        Map<String, String> result = null;
        if (isSortedTypeProvided && sortType.equals("byCount")) {
            if (longList != null) {
                result = mapByCount(longList, inputType);
            } else {
                result = mapByCount(stringList, inputType);
            }
            printByCount(result, inputType, "Total %s: %d.\n", stringInputs.size(), outPutFileProvided);

        } else if (!isSortedTypeProvided || sortType.equals("natural")) {
            if (longList != null) {
                result = mapNatural(longList);
            } else {
                result = mapNatural(stringList);
            }
            printNatural(result, inputType, "Total %s: %d.\n", stringInputs.size(), outPutFileProvided);
        }
        return result;
    }

    private static void printNatural(Map<String, String> result, InputType inputType, String s, int i, String outPutFileProvided) {
        switch (inputType) {
            case LINE:
                System.out.print(String.format(s, "lines", i));
                extracted1(result, i, outPutFileProvided);
                break;
            case LONG:
                System.out.print(String.format(s, "numbers", i));
                extracted1(result, i, outPutFileProvided);
                break;
            case WORD:
                System.out.print(String.format(s, "words", i));
                extracted1(result, i, outPutFileProvided);
                break;
        }
    }

    private static void extracted1(Map<String, String> result, int i, String outPutFileProvided) {
        if (outPutFileProvided != null) {
            System.out.println("Printed to outputfile");
            File file = new File(outPutFileProvided);
            try (PrintWriter printWriter = new PrintWriter(file)) {
                printWriter.println("Sorted data:");
                for (Map.Entry<String, String> entry : result.entrySet()) {
                    printWriter.print(entry.getValue() + " ");
                }
            } catch (IOException e) {
                System.out.println("File cannot be found and created!");
            }
        }
        System.out.print("Sorted data:");
        for (Map.Entry<String, String> entry : result.entrySet()) {
            System.out.print(entry.getValue() + " ");
        }
    }

    private static void printByCount(Map<String, String> result, InputType inputType, String msg, int i, String outPutFileProvided) {
        switch (inputType) {
            case LINE:
                extracted(result, i, outPutFileProvided, String.format(msg, "lines", i));
                break;
            case LONG:
                extracted(result, i, outPutFileProvided, String.format(msg, "numbers", i));
                break;
            case WORD:
                extracted(result, i, outPutFileProvided, String.format(msg, "words", i));
                break;
        }
    }

    private static void extracted(Map<String, String> result, int i, String outputFileProvided, String msg) {
        if (outputFileProvided != null) {
            File file = new File(outputFileProvided);
            try (FileWriter writer = new FileWriter(file, true)) {
                writer.write(msg);
                for (Map.Entry<String, String> entry : result.entrySet()) {
                    double tempProc = ((Double.parseDouble(entry.getValue()) / i) * 100);
                    BigDecimal bigDecimal = new BigDecimal(tempProc).setScale(0, RoundingMode.HALF_UP);
                    String proc = String.valueOf(bigDecimal) + "%";
                    writer.write(String.format("%s: %s time(s), %s", entry.getKey(), entry.getValue(), proc));
                    writer.write("\n");
                }
            } catch (IOException e) {
                System.out.println("File cannot be found and created!");
            }
        } else {
            System.out.print(msg);
            for (Map.Entry<String, String> entry : result.entrySet()) {
                double tempProc = ((Double.parseDouble(entry.getValue()) / i) * 100);
                BigDecimal bigDecimal = new BigDecimal(tempProc).setScale(0, RoundingMode.HALF_UP);
                String proc = String.valueOf(bigDecimal) + "%";
                System.out.println(String.format("%s: %s time(s), %s", entry.getKey(), entry.getValue(), proc));
            }
        }
    }

    private static <T> Map<String, String> mapNatural(List<T> list) {
        Map<String, String> result = new HashMap<>();
        int count = 0;
        if (list.get(0).getClass().toString().equals("class java.lang.Long")) {
            List<Long> collect = list.stream().mapToLong(value -> Long.parseLong(value.toString())).boxed().sorted().collect(Collectors.toList());
            for (Long l : collect) {
                result.put(String.valueOf(count), String.valueOf(l));
                count++;
            }
        } else {
            List<String> collect = list.stream().map(Objects::toString).sorted().collect(Collectors.toList());
            for (String l : collect) {
                result.put(String.valueOf(count), l);
                count++;
            }
        }
        return result;
    }

    private static <T> Map<String, String> mapByCount(List<T> inputList, InputType inputType) {
        List<String> collect = inputList.stream().map(Objects::toString).collect(Collectors.toList());
        Collections.sort(collect);
        Map<String, String> result = new HashMap<>();
        for (String s : collect) {
            if (result.containsKey(s)) {
                result.put(s, String.valueOf(1 + Integer.parseInt(result.get(s))));
            } else {
                result.put(s, String.valueOf(1));
            }
        }
        result = sortMapByKeyAndValue(result, inputType);
        return result;
    }

    private static Map<String, String> sortMapByKeyAndValue(Map<String, String> result, InputType inputType) {
        if (inputType.name().equals("LONG")) {
            Map<String, LongInput> convert = MapConverter.convert(result);
            convert = convert.entrySet().stream()
                    .sorted(Map.Entry.comparingByValue(LongInput::compareTo))
                    .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));
            Map<String, String> stringStringMap = MapConverter.convert1(convert);
            return stringStringMap;
        } else {
            return result.entrySet().stream().sorted((o1, o2) -> {
                if (o1.getKey().compareTo(o2.getKey()) == 0 && o1.getValue().compareTo(o2.getValue()) == 0) {
                    return 0;
                } else if (o1.getKey().compareTo(o2.getKey()) > 0 && o1.getValue().compareTo(o2.getValue()) > 0) {
                    return 1;
                } else if (o1.getKey().compareTo(o2.getKey()) > 0 && o1.getValue().compareTo(o2.getValue()) == 0) {
                    return 1;
                } else if (o1.getKey().compareTo(o2.getKey()) > 0 && o1.getValue().compareTo(o2.getValue()) < 0) {
                    return -1;
                } else if (o1.getKey().compareTo(o2.getKey()) < 0 && o1.getValue().compareTo(o2.getValue()) == 0) {
                    return -1;
                } else if (o1.getKey().compareTo(o2.getKey()) < 0 && o1.getValue().compareTo(o2.getValue()) > 0) {
                    return 1;
                } else if (o1.getKey().compareTo(o2.getKey()) < 0 && o1.getValue().compareTo(o2.getValue()) < 0) {
                    return -1;
                }
                return 0;
            }).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));
        }
    }
}

class MapConverter {
    public static Map<String, LongInput> convert(Map<String, String> oldMap) {
        Map<String, LongInput> ret = new LinkedHashMap<>();
        for (Map.Entry<String, String> entry : oldMap.entrySet()) {
            ret.put(entry.getKey(), new LongInput(Long.parseLong(entry.getKey()), entry.getValue()));
        }
        return ret;
    }

    public static Map<String, String> convert1(Map<String, LongInput> oldMap) {
        Map<String, String> ret = new LinkedHashMap<>();
        for (Map.Entry<String, LongInput> entry : oldMap.entrySet()) {
            LongInput value = entry.getValue();
            ret.put(String.valueOf(value.getKey()), value.getValue());
        }
        return ret;
    }
}

class LongInput implements Comparable<LongInput> {

    private long key;
    private String value;

    public LongInput(long key, String value) {
        this.key = key;
        this.value = value;
    }

    public String getValue() {
        return this.value;
    }

    public long getKey() {
        return this.key;
    }

    @Override
    public int compareTo(LongInput o) {
        if (this.key > o.getKey() && this.value.compareTo(o.getValue()) == 0) {
            return 1;
        } else if (this.key > o.getKey() && this.value.compareTo(o.getValue()) < 0) {
            return -1;
        } else if (this.key > o.getKey() && this.value.compareTo(o.getValue()) > 0) {
            return 1;
        } else if (this.key < o.getKey() && this.value.compareTo(o.getValue()) == 0) {
            return -1;
        } else if (this.key < o.getKey() && this.value.compareTo(o.getValue()) < 0) {
            return -1;
        } else if (this.key < o.getKey() && this.value.compareTo(o.getValue()) > 0) {
            return 1;
        }
        return 0;
    }

    @Override
    public String toString() {
        return this.key + " " + this.value;
    }
}

enum InputType {
    LONG,
    WORD,
    LINE
}
