import org.yaml.snakeyaml.Yaml;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

public class ReadYamlFile {

    public static void main(String[] args) {
        // Provide the path to your YAML file
        String yamlFilePath = "D:\\ProjectTest\\FileWrite\\application.yml";

        // Read YAML file and print its content
        try (InputStream input = new FileInputStream(yamlFilePath)) {
            Yaml yaml = new Yaml();
            Map<String, Object> yamlData = yaml.load(input);

            // Print YAML content
            System.out.println("YAML Content:");
            System.out.println(yamlData);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
