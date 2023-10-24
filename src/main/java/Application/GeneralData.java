package Application;
public class GeneralData {
    //public String logOutputPath = "/home/arthur/lri/output.log";
    //public String portFile = "/home/arthur/lri/port.txt";
    public String portFile = "/usr/local/execpipe/port.txt";
    public String locationsConfDir = "/root/Documents/configuracoesservidores/server_dev/nginx/";
    //public String locationsConfDir = "/home/arthur/";
    public String locationsConf = locationsConfDir + "locations.conf";
    public int dockerExitPort;
    public String pipefile = "pipefile";
    public String serialNumber;


    public String originalLocations = "location /selatiot/sae_cadastro_front {\n" +
            "      proxy_pass http://sae_cadastro_front:12425;\n" +
            "      rewrite ^/sae_cadastro_front(.*)$ $1 break;\n" +
            "   }\n" +
            "\n" +
            "   location /sae_cadastro_front/ {\n" +
            "      proxy_pass http://sae_cadastro_front:12425;\n" +
            "      rewrite ^/sae_cadastro_front(.*)$ $1 break;\n" +
            "   }\n" +
            "\n" +
            "    location /selatiot/sae_monitoramento_front {\n" +
            "      proxy_pass http://sae_monitoramento_front:12430;\n" +
            "      rewrite ^/sae_monitoramento_front(.*)$ $1 break;\n" +
            "   }\n" +
            "\n" +
            "   location /sae_monitoramento_front/ {\n" +
            "      proxy_pass http://sae_monitoramento_front:12430;\n" +
            "      rewrite ^/sae_monitoramento_front(.*)$ $1 break;\n" +
            "   }\n" +
            "\n" +
            "   location /selatiot/twinsfront {\n" +
            "      proxy_pass http://twinsfront:4200;\n" +
            "      rewrite ^/twinsfront(.*)$ $1 break;\n" +
            "   }\n" +
            "\n" +
            "    location /twinsfront/ {\n" +
            "      proxy_pass http://twinsfront:4200;\n" +
            "      rewrite ^/twinsfront(.*)$ $1 break;\n" +
            "   }\n" +
            "\n" +
            "   location /selatiot/dpiotfront {\n" +
            "      proxy_pass http://dpiotfront:4000;\n" +
            "      rewrite ^/selatiot/dpiotfront(.*)$ $1 break;\n" +
            "   }\n" +
            "\n" +
            "    location /dpiotfront/ {\n" +
            "      proxy_pass http://dpiotfront:4000;\n" +
            "      rewrite ^/dpiotfront(.*)$ $1 break;\n" +
            "   }\n" +
            "location /express/ {\n" +
            "      proxy_set_header X-Script-Name /pgadmin4;\n" +
            "      proxy_set_header X-Scheme $scheme;\n" +
            "      proxy_set_header Host $host;\n" +
            "      proxy_pass http://mongo-express:8081;\n" +
            "      proxy_redirect off;\n" +
            "   }\n" +
            "\n" +
            "   location /jenkins {\n" +
            "      proxy_pass http://10.3.192.50:8080;\n" +
            "      proxy_set_header Host $host;\n" +
            "      proxy_set_header X-Real-IP $remote_addr;\n" +
            "   }\n" +
            "\n" +
            "   location /selatiot/auth {\n" +
            "      proxy_pass http://selatiot_portal_backend:4001;\n" +
            "      rewrite ^/selatiot/auth(.*)$ $1 break;\n" +
            "   }\n" +
            "\n" +
            "   location / {\n" +
            "        proxy_pass http://selatiot_portal_front:4444;\n" +
            "   }\n";

    public String newNginxLocations = "";
}
