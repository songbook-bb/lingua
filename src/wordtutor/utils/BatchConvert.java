package wordtutor.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collection;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;

public class BatchConvert {
  static final int errorReturnCode = -1;
  static final int allRightReturnCode = 0;
  static final int skippedMethodReturnCode = 777;
  static final String removeWin = "del ";     
  static final String removeLinux = "rm -r ";
  
  Logger logger = Logger.getLogger(BatchConvert.class);  

  public int runWave2mp3(Double ampFactor) {
    if (!Util.TRUE.equalsIgnoreCase(Util.getAppProperty("PERFORM.CONVERT"))) {
      return skippedMethodReturnCode;
    }
    //logger.debug("ampFactor=" + ampFactor);
    String os = System.getProperty("os.name").toLowerCase();
    //logger.debug("Detected OS : '" + os + "'");
    if (os.indexOf(Util.getAppProperty("LINUX.PATTERN")) > 0) {
      writeBatch(true, ampFactor);
      logger.debug("written LINUX batch : ");
    }
    if (os.indexOf(Util.getAppProperty("WIN.PATTERN")) > 0) {
      writeBatch(false, ampFactor);
      //logger.debug("written WIN batch : ");
    }
    return executeBatch(os);
  }

  private void writeBatch(boolean isLinux, Double ampFactor) {
    String[] extensions = { "wav" };
    @SuppressWarnings("unchecked")
    Collection<File> waveList = (Collection<File>) FileUtils.listFiles(
        new File(Util.getAppProperty("RESOURCE.PATH") + File.separator
            + Util.getAppProperty("MP3.PATH")), extensions, true);
    ArrayList<String> stringList = new ArrayList<String>();

    try {

      if (waveList.size() > 0) {
        for (File f : waveList) {
          String argFile = f.getCanonicalPath();
          String scale = "";
          // tylko pod tymi warunkami będzie skalował wav-a
          if (isLinux && waveList.size() == 1 && ampFactor != null
              && ampFactor > 1) {
            scale = " " + Util.getAppProperty("LAME.SCALE") + " "
                + ampFactor + " ";
          }
          logger.debug((isLinux ? Util.getAppProperty("LAME.LINUX")
              : Util.surroundByQuotes(System.getProperty("user.dir").getBytes("CP1250") + File.separator
                  + Util.getAppProperty("LAME.WIN")))
              + " "
              + Util.getAppProperty("LAME.SWITCH")
              + " "
              + scale
              + Util.surroundByQuotes(argFile).getBytes("CP1250")
              + " "
              + Util.surroundByQuotes(argFile.substring(0,
                  argFile.indexOf(Util.getAppProperty("WAVE.EXTENSION")))
              + Util.getAppProperty("MP3.EXTENSION"))
              + System.getProperty("line.separator"));
          stringList.add((isLinux ? Util.getAppProperty("LAME.LINUX")
              : Util.surroundByQuotes(System.getProperty("user.dir") + File.separator
                  + Util.getAppProperty("LAME.WIN")))
              + " "
              + Util.getAppProperty("LAME.SWITCH")
              + " "
              + scale
              + Util.surroundByQuotes(argFile)
              + " "
              + Util.surroundByQuotes(argFile.substring(0,
                  argFile.indexOf(Util.getAppProperty("WAVE.EXTENSION")))
              + Util.getAppProperty("MP3.EXTENSION"))
              + System.getProperty("line.separator"));
        }
        for (File f : waveList) {
          String delFile = Util.surroundByQuotes(f.getCanonicalPath());
          logger.debug(" DELETE "+((isLinux) ? removeLinux : removeWin) + " " + delFile + System.getProperty("line.separator"));
          stringList.add(((isLinux) ? removeLinux : removeWin) + " " + delFile + System.getProperty("line.separator"));
        }
      }
      createBatchFile(stringList, isLinux);
      
    } catch (IOException ioe) {
  	  logger.error(ioe.getMessage(), ioe);
  	  logger.error(ioe.getStackTrace());	
    }
  }

  public void createBatchFile(ArrayList<String> stringList, boolean isLinux)
      throws IOException {

    String batchFileName = Util.getAppProperty("RESOURCE.PATH")
        + File.separator
        + Util.getAppProperty("MP3.PATH")
        + File.separator
        + (isLinux ? Util.getAppProperty("CONVERT.LINUX") : Util.getAppProperty("CONVERT.WIN"));
    FileWriter outFile = new FileWriter(batchFileName);
    PrintWriter out = new PrintWriter(outFile);
    for (String s : stringList) {
      out.print(s);
    }
    out.close();
    outFile.close();
    if (isLinux) {
      try {
        Runtime r = Runtime.getRuntime();
        String chmodString = "chmod +x " + batchFileName;
        logger.debug("Execute : " + chmodString);
        Process changePermissions = r.exec(chmodString);
        changePermissions.waitFor();
      } catch (InterruptedException ie) {
    	logger.error(ie.getMessage(), ie);
    	logger.error(ie.getStackTrace());            	  
      }
    }
  }  
  
  class StreamGobbler extends Thread
  {
      InputStream is;
      String type;
      
      StreamGobbler(InputStream is, String type)
      {
          this.is = is;
          this.type = type;
      }
      
      public void run()
      {
          try
          {
              InputStreamReader isr = new InputStreamReader(is);
              BufferedReader br = new BufferedReader(isr);
              String line=null;
              while ( (line = br.readLine()) != null)
                  logger.debug(type + ">" + line);    
              } catch (IOException ioe) {
            	  logger.error(ioe.getMessage(), ioe);
            	  logger.error(ioe.getStackTrace());            	    
              }
      }
  }

  public int executeBatch(String os) {
    try {
      String script = "";
      String[] cmd = new String[3];      
      if (os.indexOf(Util.getAppProperty("LINUX.PATTERN")) > 0) {
        script = Util.getAppProperty("BIN.BASH") + " "
            + Util.getAppProperty("RESOURCE.PATH") + File.separator
            + Util.getAppProperty("MP3.PATH") + File.separator
            + "convert_linux.sh";
      }
      if (os.indexOf(Util.getAppProperty("WIN.PATTERN")) > 0) {
        script = 
            System.getProperty("user.dir") + File.separator
            + Util.getAppProperty("RESOURCE.PATH") + File.separator
            + Util.getAppProperty("MP3.PATH") + File.separator
            + "convert_win.bat";
        cmd[0] =  Util.getAppProperty("WIN.CMD");
        cmd[1] =  Util.getAppProperty("WIN.CMD.SWITCH");
        cmd[2] =  script;                
      }

      if (script.length() > 0) {
        
        Process process = null;        
        if (os.indexOf(Util.getAppProperty("LINUX.PATTERN")) > 0) {
          logger.info("Running Linux script : " + script);        
          process = Runtime.getRuntime().exec(script);              
        } else if (os.indexOf(Util.getAppProperty("WIN.PATTERN")) > 0) {
          //logger.debug("Running Windows batch : " + cmd);        
          process = Runtime.getRuntime().exec(cmd);
        } else {
          logger.error("Unsupported system: "+os);
          Util.exitVM(-1);          
        }
        // show BATCH error/output 
        StreamGobbler errorGobbler = new 
        StreamGobbler(process.getErrorStream(), "ERROR");                  
        StreamGobbler outputGobbler = new 
        StreamGobbler(process.getInputStream(), "OUTPUT");
          
        errorGobbler.start();
        outputGobbler.start();
                              
        int exitVal = process.waitFor();
        logger.debug("Exit BATCH Value: " + exitVal);        
        return allRightReturnCode;
      }
      logger.debug("Empty SCRIPT string for OS :" + os);
      return errorReturnCode;
    } catch (Exception e) {
  	  logger.error(e.getMessage(), e);
  	  logger.error(e.getStackTrace());            	  
      return errorReturnCode;
    }

  }
  
  public static void main(String args[]) throws IOException {
    // create linux batch file
    Logger logger = Logger.getLogger(BatchConvert.class);
    String os = System.getProperty("os.name").toLowerCase();
    new BatchConvert().executeBatch(os);
    logger.debug("Finished ... BatchConvert().runWave2mp3()");
  }

}