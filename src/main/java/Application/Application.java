package Application;
import java.io.*;

public class Application {
    public static final GeneralData GENERAL_DATA = new GeneralData();
    public static void main(String[] args) throws IOException, InterruptedException {
        do{
            startNewMonitoramentoStatement();
        }while(wasNotStarted());

    }

    public static void startNewMonitoramentoStatement() throws IOException, InterruptedException {
        GENERAL_DATA.dockerExitPort = getNextPort();
        ProcessBuilder processBuilder = new ProcessBuilder();
        String dockerRunString = "docker run -d -p " + GENERAL_DATA.dockerExitPort + ":12428 --name sae_monitoramento_back-" + GENERAL_DATA.dockerExitPort + " -it " + "sae_monitoramento_back";
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

                writeContainerUp.write(stdoutLine);
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

}