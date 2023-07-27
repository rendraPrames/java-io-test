import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        String institution = getInstitutionFromUserInput(scanner);
        Path filePath = getInputFilePath();
        Map<String, List<String>> environmentPortsMap = readOfflinePorts(filePath, institution);
        String outputMessage = createOutputMessage(institution, environmentPortsMap);
        sendMessageToInstitution(outputMessage);
    }

    private static String getInstitutionFromUserInput(Scanner scanner) {
        System.out.print("Masukkan nama institusi (MDR/BNI): ");
        return scanner.nextLine().toUpperCase();
    }

    private static Path getInputFilePath() {
        String projectDirectory = System.getProperty("user.dir");
        String filePath = projectDirectory + "/input/Data Alert.txt";
        return Paths.get(filePath);
    }

    private static Map<String, List<String>> readOfflinePorts(Path filePath, String institution) {
        Map<String, List<String>> environmentPortsMap = new LinkedHashMap<>();

        try (BufferedReader br = new BufferedReader(new FileReader(filePath.toFile()))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] data = line.split(";");
                if (data.length >= 5 && data[4].trim().equalsIgnoreCase("Offline") && data[0].trim().equalsIgnoreCase(institution)) {
                    String environment = data[1].trim();
                    String port = "Port " + data[2].trim() + " terpantau Offline";
                    environmentPortsMap.computeIfAbsent(environment, k -> new ArrayList<>()).add(port);
                }
            }
        } catch (IOException e) {
            System.err.println("Terjadi kesalahan saat membaca file." + e.getMessage());
        }

        return environmentPortsMap;
    }

    private static String createOutputMessage(String institution, Map<String, List<String>> environmentPortsMap) {
        StringBuilder sb = new StringBuilder("Selamat siang rekan Bank " + institution + ",\n");
        sb.append("Mohon bantuannya untuk Sign on pada envi berikut:\n\n");

        for (Map.Entry<String, List<String>> entry : environmentPortsMap.entrySet()) {
            String environment = entry.getKey();
            List<String> ports = entry.getValue();

            for (String port : ports) {
                sb.append("- Envi ").append(environment).append(" ").append(port).append("\n");
            }
        }

        sb.append("\nTerimakasih");
        return sb.toString();
    }

    private static void sendMessageToInstitution(String message) {
        System.out.println(message);
    }
}