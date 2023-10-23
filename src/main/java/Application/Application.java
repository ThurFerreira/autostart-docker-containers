package Application;
import java.io.*;
import java.nio.file.Files;
import java.security.spec.ECField;
import java.util.ArrayList;
import java.util.List;

public class Application {
    public static final GeneralData GENERAL_DATA = new GeneralData();
    public static void main(String[] args) throws IOException, InterruptedException {
            startNewMonitoramentoStatement(args[0]);
        //GENERAL_DATA.dockerExitPort = 5043;
        //addContainerToServer("back");
        //    removeContainerFromServer(String.valueOf(5042));
    }

    public static void startNewMonitoramentoStatement(String containerType) throws IOException, InterruptedException {
        GENERAL_DATA.dockerExitPort = getNextPort();
        String dockerRunString = "docker run -d -p " + GENERAL_DATA.dockerExitPort + ":12428 --name sae_monitoramento_" + containerType + "-" + GENERAL_DATA.dockerExitPort + " -it " + "sae_monitoramento_" + containerType;
        String command = "echo " + dockerRunString + " > " + GENERAL_DATA.pipefile;

        Process process = Runtime.getRuntime().exec(dockerRunString);

        try{
            // Read the standard output
            BufferedReader stdInput = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String stdoutLine;
            BufferedWriter write = new BufferedWriter(new FileWriter("output.txt", false));
            BufferedWriter writeContainerUp = new BufferedWriter(new FileWriter("containers-up.txt", true));

            while ((stdoutLine = stdInput.readLine()) != null) {
                write.write(stdoutLine);
                write.newLine();

                writeContainerUp.write(GENERAL_DATA.dockerExitPort+";"+stdoutLine);
                writeContainerUp.newLine();
            }

            write.close();
            writeContainerUp.close();
        }catch (Exception e){
            e.printStackTrace();
        }

        try{
            // Read the standard error
            BufferedWriter write = new BufferedWriter(new FileWriter("error.txt", false));
            BufferedReader stdError = new BufferedReader(new InputStreamReader(process.getErrorStream()));
            String stderrLine;
            while ((stderrLine = stdError.readLine()) != null) {
                System.out.println("error: " + stderrLine);
                write.write(stderrLine);
                write.newLine();
            }

            write.close();
        }catch (Exception e){
            e.printStackTrace();
        }

        // Wait for the command to finish
        int exitCode = process.waitFor();
        System.out.println("ExitCode: " + exitCode + "exitValue" + process.exitValue());
    }

    public static int getNextPort(){
        try(BufferedReader reader = new BufferedReader(new FileReader(GENERAL_DATA.portFile))){
            String line;
            int newPort = 0;

            line = reader.readLine();

            try(BufferedWriter writer = new BufferedWriter(new FileWriter(GENERAL_DATA.portFile, false))){
                newPort = Integer.parseInt(line);
                newPort++;
                writer.write(String.valueOf(newPort));

            }catch(Exception e){
                System.out.println("Error writing portFile" + e.getMessage());
            }

            return newPort;

        } catch (FileNotFoundException e) {
            return initializePortFile();
        } catch (IOException e) {
            System.out.println("Error writing portFile" + e.getMessage());
            throw new RuntimeException(e);
        }
    }

    public static int initializePortFile(){
        int initialPort = 5000;
        try(BufferedWriter writer = new BufferedWriter(new FileWriter(GENERAL_DATA.portFile, false))){
            writer.write(String.valueOf(initialPort));

        }catch(Exception f){
            System.out.println("Error writing portFile: " + f.getMessage());
        }

        return initialPort;
    }
    public static boolean wasNotStarted(){
        File file = new File("output.txt");
        return file.length() == 0;
    }

    public static void addContainerToServer(String containerType){
        try(BufferedWriter locationsConf = new BufferedWriter(new FileWriter(GENERAL_DATA.locationsConf, true))){
            locationsConf.write(getNewNginxContainerString(containerType));

        }catch(Exception f){
            System.out.println("Error writing portFile: " + f.getMessage());
        }
    }

    public static void removeContainerFromServer(String containerPort){
        File locationsDotConf = new File(GENERAL_DATA.locationsConf);

        try(BufferedReader reader = new BufferedReader(new FileReader(locationsDotConf))) {
            File temporaryFile = new File(GENERAL_DATA.locationsConfDir + "tempLocations.conf");
            BufferedWriter writer = new BufferedWriter(new FileWriter(temporaryFile, false));
            String line;

            while((line = reader.readLine()) != null){
                if(!line.contains("#"+containerPort)){
                    writer.write(line + "\n");
                }
            }

            writer.close();

            if(Files.size(temporaryFile.toPath()) != 0){
                locationsDotConf.delete();
            }


            File rename = new File(GENERAL_DATA.locationsConf);
            temporaryFile.renameTo(rename);

        }catch (Exception e){

        }
    }

    public static String getNewNginxContainerString(String containerType){
        String identificationTAG = "\t#" + GENERAL_DATA.dockerExitPort + "\n";
        String container = "sae_monitoramento_" + containerType + "-";
        String monitoramentoHeader = "#" + container + GENERAL_DATA.dockerExitPort + identificationTAG;
        return          identificationTAG +
                        monitoramentoHeader +
                        "   location /selatiot/" + container + GENERAL_DATA.dockerExitPort + "{" + identificationTAG +
                        "      proxy_pass http://" + container + GENERAL_DATA.dockerExitPort + ":" + GENERAL_DATA.dockerExitPort + ";" + identificationTAG +
                        "      rewrite ^/" + container + GENERAL_DATA.dockerExitPort + "(.*)$ $1 break;" + identificationTAG +
                        "   }" + identificationTAG +
                        identificationTAG +
                        "   location /" + container + GENERAL_DATA.dockerExitPort + "{" + identificationTAG  +
                        "      proxy_pass http://" + container + GENERAL_DATA.dockerExitPort + ":" + GENERAL_DATA.dockerExitPort + ";" + identificationTAG  +
                        "      rewrite ^/" + container + GENERAL_DATA.dockerExitPort + "(.*)$ $1 break;"  + identificationTAG  +
                        "   }" + identificationTAG +
                        identificationTAG;
    }


}