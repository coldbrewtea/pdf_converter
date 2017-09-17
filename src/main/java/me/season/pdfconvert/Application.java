package me.season.pdfconvert;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Created by zhangshichen on 2017/8/2.
 */
@SpringBootApplication
public class Application {

    // web service main class
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    // executable jar main class
    /*public static void main(String[] args) throws IOException, ParseException {
        Options options = new Options();
        Option optDest = Option.builder("o")
                .required(false)
                .hasArg()
                .argName("o")
                .desc("output path")
                .build();
        options.addOption(optDest);

        CommandLineParser parser = new DefaultParser();
        CommandLine cmd = parser.parse(options, args);

        String destination = cmd.getOptionValue(optDest.getArgName());
        String[] ss = cmd.getArgs();
        for (String s : ss) {
            String target = s.substring(0, s.lastIndexOf(".")) + ".pdf";
            if (destination != null) {
                if (!destination.endsWith("\\")) {
                    destination += "\\";
                }
                target = destination + s.substring(s.lastIndexOf("\\") + 1, s.lastIndexOf(".")) + ".pdf";
                File f = new File(target);
                if (!f.getParentFile().exists()) {
                    f.mkdirs();
                }
            }
            System.out.println("target:" + target);
            ConverterManager.convert(s, target);
        }
    }*/

}
