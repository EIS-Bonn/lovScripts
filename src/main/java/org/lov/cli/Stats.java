package org.lov.cli;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

import org.jongo.Jongo;
import org.jongo.MongoCollection;
import org.jongo.MongoCursor;
import org.lov.objects.Language;
import org.lov.objects.Pilot;
import org.lov.objects.StatLang;
import org.lov.objects.StatTag;
import org.lov.objects.StatVocab;
import org.lov.objects.Vocabulary;
import org.lov.objects.VocabularyVersionWrapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import arq.cmdline.CmdGeneral;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;


/**
 * ...
 * 
 */
public class Stats extends CmdGeneral {
	private final static Logger log = LoggerFactory.getLogger(Stats.class);
	
	private String dbName;
	private static MongoCollection vocabCollection;
	private static MongoCollection langCollection;
	//trillos
	private static MongoCollection tagCollection;
	private static MongoCollection pilotCollection;
	private static Jongo jongo;
	
	public static void main(String... args) {
		new Stats(args).mainRun();
	}

	private String configFilePath;
	
	public Stats(String[] args) {
		super(args);
		getUsage().startCategory("Arguments");
		getUsage().addUsage("configFilePath", "absolute path for the configuration file  (e.g. /home/...)");
	}
	
	@Override
    protected String getCommandName() {
		return "stats";
	}
	
	@Override
	protected String getSummary() {
		return getCommandName() + " configFilePath (e.g. /home/...)";
	}

	@SuppressWarnings("deprecation")
	@Override
	protected void processModulesAndArgs() {
		/*if (getPositional().size() < 1) {
			doHelp();
		}
		configFilePath = getPositionalArg(0);*/
		//trillos
		configFilePath = configPath.configFilePath;
		//load properties from the config file
		try {
			Properties lovConfig = new Properties();
			File file = new File(configFilePath);
			InputStream is = new FileInputStream(file);
			lovConfig.load(is);
			String uriString = "mongodb://" + lovConfig.getProperty("MONGO_DB_USER_PASSWORD") + "@" + lovConfig.getProperty("MONGO_DB_HOST") + ":" + Integer.parseInt(lovConfig.getProperty("MONGO_DB_PORT")) + "/?authSource=admin";
			MongoClientURI uri = new MongoClientURI(uriString);
			MongoClient mongoClient = new MongoClient(uri);
			dbName = lovConfig.getProperty("MONGO_DB_INSTANCE");
			jongo = new Jongo(mongoClient.getDB(dbName));
			vocabCollection = jongo.getCollection("vocabularies");
			langCollection = jongo.getCollection("languages");
			//trillos
			tagCollection = jongo.getCollection("stattags");
			pilotCollection = jongo.getCollection("pilots");
			
			//mongoClient.close();
		} catch (FileNotFoundException e) {
			log.error(e.getMessage());
		} catch (IOException e) {
			log.error(e.getMessage());
		}
	}

	@Override
	protected void exec() {
		try{
			long startTime = System.currentTimeMillis();
			log.info("####### <Stats> #######");
			
			/* Generate number incoming links per vocabulary */
			//get the list of vocabularies and their relationships to others
			MongoCursor<Vocabulary> vocabs = vocabCollection.find().as(Vocabulary.class);
			List<Vocabulary> vocabList = new ArrayList<Vocabulary>();
			for (Vocabulary vocab : vocabs) {
				vocabList.add(vocab);
			}
			
			//get the list of languages
			MongoCursor<Language> langs = langCollection.find().as(Language.class);
			List<Language> langList = new ArrayList<Language>();
			for (Language lang : langs) {
				langList.add(lang);
			}
		        log.info("language retrieved");	

		    //trillos
			//get the list of statTags (Backup)
			MongoCursor<StatTag> tags = tagCollection.find().as(StatTag.class);
			List<StatTag> tagsList = new ArrayList<StatTag>();
			for (StatTag tag : tags) {
				tagsList.add(tag);
			}
			log.info("statTags retrieved");	
			
			//get the list of pilots (Backup)
			MongoCursor<Pilot> pilots = pilotCollection.find().as(Pilot.class);
			List<Pilot> pilotsList = new ArrayList<Pilot>();
			for (Pilot pilot : pilots) {
				pilotsList.add(pilot);
			}
			log.info("pilots retrieved");

			//create a list of vocabStat and compute the number of incoming links for each
			List<StatVocab> statVocabs = new ArrayList<StatVocab>();
			for (Vocabulary vocab : vocabList) {
				StatVocab statVoc = new StatVocab(vocab.getUri(),vocab.getNsp(), vocab.getPrefix());
				statVocabs.add(statVoc);
			}
			
			//replace the object in MongoDB or create it if it does not exist
			MongoCollection statCollection = DropCreateCollection("statvocabularies",jongo);
			statCollection.insert(statVocabs.toArray());
			log.info("statVocabularies updated");	
			
			//create a list of tagStat and compute the number of occurrences in vocabularies
			List<StatTag> statTags = new ArrayList<StatTag>();
			
			//trillos
			//create a list of pilots and compute the number of occurrences in vocabularies
			List<Pilot> pilotTags = new ArrayList<Pilot>();
			
			//create a list of tagStat and compute the number of occurrences in vocabularies
			List<StatLang> statLangs = new ArrayList<StatLang>();
			
			for (Vocabulary vocab : vocabList) {
				StatVocab statVoc = new StatVocab(vocab.getUri(),vocab.getNsp(), vocab.getPrefix());
				if(vocab.getTags()!=null){
					for (String tag : vocab.getTags()) {
						StatTag statTag = new StatTag(tag);
						statTag.addOccurrence();
						if(statTags.indexOf(statTag)>-1)statTags.get(statTags.indexOf(statTag)).addOccurrence();
						else statTags.add(statTag);
					}
				}
				//trillos
				if(vocab.getPilots()!=null){
					for (String pilot : vocab.getPilots()) {
						Pilot pilotTag = new Pilot(pilot);
						pilotTag.addOccurrence();
						if(pilotTags.indexOf(pilotTag)>-1)pilotTags.get(pilotTags.indexOf(pilotTag)).addOccurrence();
						else pilotTags.add(pilotTag);
					}
				}
				VocabularyVersionWrapper lVersion = vocab.getLastVersion();
				if(lVersion!=null){
					statVoc.addOutRelDisjunc(lVersion.getRelDisjunc(), statCollection);
					statVoc.addOutRelEquivalent(lVersion.getRelEquivalent(), statCollection);
					statVoc.addOutRelExtends(lVersion.getRelExtends(), statCollection);
					statVoc.addOutRelGeneralizes(lVersion.getRelGeneralizes(), statCollection);
					statVoc.addOutRelImports(lVersion.getRelImports(), statCollection);
					statVoc.addOutRelMetadata(lVersion.getRelMetadata(), statCollection);
					statVoc.addOutRelSpecializes(lVersion.getRelSpecializes(), statCollection);
					
					if(lVersion.getLanguageIds()!=null){
						for (String lang : lVersion.getLanguageIds()) {
							if(lang!=null && !lang.contains("null")){
								StatLang statLang = new StatLang(lang);
								statLang.addOccurrence();
								if(statLangs.indexOf(statLang)>0)statLangs.get(statLangs.indexOf(statLang)).addOccurrence();
								else statLangs.add(statLang);
							}
						}
					}
				}
				for (Vocabulary voc : vocabList) {
					VocabularyVersionWrapper lastVersion = voc.getLastVersion();
					if(lastVersion!=null){
						if(lastVersion.getRelDisjunc()!=null && (lastVersion.getRelDisjunc().contains(statVoc.getNsp()) || lastVersion.getRelDisjunc().contains(statVoc.getUri()) ))statVoc.addIncomRelDisjunc(voc.getUri(),statCollection);
						if(lastVersion.getRelEquivalent()!=null && (lastVersion.getRelEquivalent().contains(statVoc.getNsp()) || lastVersion.getRelEquivalent().contains(statVoc.getUri()) ))statVoc.addIncomRelEquivalent(voc.getUri(),statCollection);
						if(lastVersion.getRelExtends()!=null && (lastVersion.getRelExtends().contains(statVoc.getNsp()) || lastVersion.getRelExtends().contains(statVoc.getUri()) ))statVoc.addIncomRelExtends(voc.getUri(),statCollection);
						if(lastVersion.getRelGeneralizes()!=null && (lastVersion.getRelGeneralizes().contains(statVoc.getNsp()) || lastVersion.getRelGeneralizes().contains(statVoc.getUri()) ))statVoc.addIncomRelGeneralizes(voc.getUri(),statCollection);
						if(lastVersion.getRelImports()!=null && (lastVersion.getRelImports().contains(statVoc.getNsp()) || lastVersion.getRelImports().contains(statVoc.getUri()) ))statVoc.addIncomRelImports(voc.getUri(),statCollection);
						if(lastVersion.getRelMetadata()!=null && (lastVersion.getRelMetadata().contains(statVoc.getNsp()) || lastVersion.getRelMetadata().contains(statVoc.getUri()) ))statVoc.addIncomRelMetadata(voc.getUri(),statCollection);
						if(lastVersion.getRelSpecializes()!=null && (lastVersion.getRelSpecializes().contains(statVoc.getNsp()) || lastVersion.getRelSpecializes().contains(statVoc.getUri()) ))statVoc.addIncomRelSpecializes(voc.getUri(),statCollection);
					}					
				}
				statVocabs.add(statVoc);
			}
			
			//update statVocabs
			for (StatVocab statVocab : statVocabs) {
				statCollection.update("{uri:#}", statVocab.getUri()).with(statVocab);
			}
			
			//change id for labels of languages and then populate mongodb
			for (Iterator<StatLang> iterator = statLangs.iterator(); iterator.hasNext();) {
				StatLang statLang = (StatLang) iterator.next();
				statLang.setLabel(getLanguageCode(langList, statLang.getLabel()));
			}
			MongoCollection statLangsCollection = DropCreateCollection("statlanguages",jongo);
			statLangsCollection.insert(statLangs.toArray());
			log.info("statLanguages updated");

			//trillos
			//change NbOccurrences for labels of statTags and then populate mongodb
			for (Iterator<StatTag> iterator = tagsList.iterator(); iterator.hasNext();) {
				StatTag tagList = (StatTag) iterator.next();
				tagList.setNbOccurrences(getTagOccurrences(statTags, tagList.getLabel()));
			}
			
			//change NbOccurrences for labels of pilots and then populate mongodb
			for (Iterator<Pilot> iterator = pilotsList.iterator(); iterator.hasNext();) {
				Pilot pilotList = (Pilot) iterator.next();
				pilotList.setNbOccurrences(getPilotOccurrences(pilotTags, pilotList.getName()));
			}
					
			//replace the object in MongoDB or create it if it does not exist
			MongoCollection statTagsCollection = DropCreateCollection("stattags",jongo);
			statTagsCollection.insert(tagsList.toArray());
			log.info("statTags updated");
			
			//trillos
			//replace the object in MongoDB or create it if it does not exist
			MongoCollection pilotTagsCollection = DropCreateCollection("pilots",jongo);
			pilotTagsCollection.insert(pilotsList.toArray());
			
			log.info("####### </Stats> #######");
			long estimatedTime = System.currentTimeMillis() - startTime;
			log.info("=> Stats processed in "+String.format("%d sec, %d ms", 
				    TimeUnit.MILLISECONDS.toSeconds(estimatedTime),
				    estimatedTime - TimeUnit.MILLISECONDS.toSeconds(estimatedTime)
				));
		} catch (Exception e) {
			log.error(e.getMessage());
		}
	}
	
	private MongoCollection DropCreateCollection(String collectionname, Jongo jongo){
		jongo.getCollection(collectionname).drop();
		return jongo.getCollection(collectionname);
	}
	
	private String getLanguageCode(List<Language> langList , String id){
		for (Iterator<Language> iterator = langList.iterator(); iterator.hasNext();) {
			try{
			Language language = (Language) iterator.next();
			if(language.getId().equals(id))return language.getIso639P3PCode();
			} catch (Exception e){
				log.error("language not found"+ e.getMessage());
			}
		}
		return "unknown";
	}

	//trillos
	private int getTagOccurrences(List<StatTag> tags , String label){
		for (Iterator<StatTag> iterator = tags.iterator(); iterator.hasNext();) {
			try{
				StatTag tag = (StatTag) iterator.next();
				if(tag.getLabel().equals(label)){
					return tag.getNbOccurrences();
				}
			} catch (Exception e){
				log.error("StatTag not found"+ e.getMessage());
			}
		}
		return 0;
	}
	
	//trillos	
	private int getPilotOccurrences(List<Pilot> pilots , String name){
		for (Iterator<Pilot> iterator = pilots.iterator(); iterator.hasNext();) {
			try{
				Pilot pilot = (Pilot) iterator.next();
				if(pilot.getName().equals(name)){
					return pilot.getNbOccurrences();
				}
			} catch (Exception e){
				log.error("StatTag not found"+ e.getMessage());
			}
		}
		return 0;
	}
	
}
