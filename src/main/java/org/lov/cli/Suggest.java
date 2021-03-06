package org.lov.cli;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.apache.log4j.LogManager;
import org.lov.LovBotVocabAnalyser;
import org.lov.objects.VocabularySuggest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;

import arq.cmdline.CmdGeneral;

/**
 * ...
 * 
 */
public class Suggest extends CmdGeneral {
	private final static Logger log = LoggerFactory.getLogger(Suggest.class);
	
	public static void main(String... args) {
		new Suggest(args).mainRun();
	}

	private String vocabularyURI;
	private Properties lovConfig;
	
	public Suggest(String[] args) {
		super(args);
		getUsage().startCategory("Arguments");
		//jtrillos
		getUsage().addUsage("vocabularyURI", "URI of the vocabulary (e.g. http://...)");
		//getUsage().addUsage("configFilePath", "absolute path for the configuration file  (e.g. /home/...)");
	}
	
	@Override
    protected String getCommandName() {
		return "suggest";
	}
	
	@Override
	protected String getSummary() {
		//jtrillos
		//return getCommandName() + "vocabularyURI configFilePath";
		return getCommandName() + "vocabularyURI";
	}

	@Override
	protected void processModulesAndArgs() {
		//jtrillos
		if (getPositional().size() < 1) {
			doHelp();
		}
		vocabularyURI = getPositionalArg(0);
		
		try {
			lovConfig = new Properties();
			//jtrillos
			//File file = new File(getPositionalArg(1));
			InputStream is = new FileInputStream(configPath.configFilePath);
			lovConfig.load(is);			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	protected void exec() {
		LogManager.getLogger("org.mongodb").setLevel(org.apache.log4j.Level.OFF);
		
		try {
			VocabularySuggest result = LovBotVocabAnalyser.analyseVocabURI(vocabularyURI, lovConfig);
//			result.prettyPrint(log);
			Gson gson  =new Gson();
			System.out.println(gson.toJson(result));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
