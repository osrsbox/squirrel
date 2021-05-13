package com.osrsbox.squirrel;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.FileWriter;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.HelpFormatter;

import net.runelite.cache.IndexType;
import net.runelite.cache.ItemManager;
import net.runelite.cache.NpcManager;
import net.runelite.cache.ObjectManager;
import net.runelite.cache.TextureManager;
import net.runelite.cache.definitions.ModelDefinition;
import net.runelite.cache.definitions.loaders.ModelLoader;
import net.runelite.cache.fs.Store;
import net.runelite.cache.fs.Storage;
import net.runelite.cache.fs.flat.FlatStorage;
import net.runelite.cache.fs.Index;
import net.runelite.cache.fs.Archive;
import net.runelite.cache.models.ObjExporter;

public class Nuts
{
	public static void main(String[] args) throws IOException
	{
		Options options = new Options();

		options.addOption("h", "help", false, "Print this help menu");
		options.addOption("v", "version", false, "Print the version number");
		
		options.addOption("c", "cache", true, "Load cache from specific target folder");
		options.addOption("f", "flatcache", true, "Load flat cache from specific target folder");
		
		options.addOption("i", "items", false, "Dump ItemDefinitions in JSON to specific target folder");
		options.addOption("n", "npcs", false, "Dump NpcDefinitions in JSON to specific target folder");
		options.addOption("o", "objects", false, "Dump ObjectDefinitions in JSON to specific target folder");
		options.addOption("m", "models", false, "Dump models in OBJ + MTL to specific target folder");

		CommandLineParser parser = new DefaultParser();
		CommandLine cmd;
		try
		{
			cmd = parser.parse(options, args);
		}
		catch (ParseException ex)
		{
			System.err.println("Error parsing command line options: " + ex.getMessage());
			System.exit(-1);
			return;
		}

		if (cmd.hasOption("help")) 
		{
			HelpFormatter formatter = new HelpFormatter();
			formatter.printHelp( "squirrel" , options );
			return;
		}

		if (cmd.hasOption("version")) 
		{
			String version = Nuts.class.getPackage().getImplementationVersion();
			System.out.println("squirrel - " + version);

			String runeliteCacheVersion = Nuts.class.getPackage().getImplementationTitle();
			System.out.println("runelite - " + runeliteCacheVersion);
			return;
		}

		// Load cache store
		Store store;
		if (cmd.hasOption("cache"))
		{
			String cache = cmd.getOptionValue("cache");
			System.out.println("[*] Loading cache from: " + cache);
			store = loadStore(cache);
		}
		else if (cmd.hasOption("flatcache"))
		{
			String cache = cmd.getOptionValue("flatcache");
			System.out.println("[*] Loading flatcache from: " + cache);
			store = loadStoreFlat(cache);
		}
		else
		{
			String cache = "osrs-cache";
			System.out.println("[*] Loading default flatcache from: " + cache);
			store = loadStoreFlat(cache);
		}

		// Try do something
		if (cmd.hasOption("items"))
		{
			String itemdir;
			itemdir = cmd.getOptionValue("items");

			if (itemdir == null)
			{
				itemdir = "dumped-objects";
			}

			System.out.println("[*] Dumping items to: " + itemdir);
			dumpItems(store, new File(itemdir));
		}
		else if (cmd.hasOption("npcs"))
		{
			String npcdir;
			npcdir = cmd.getOptionValue("npcs");

			if (npcdir == null)
			{
				npcdir = "dumped-npcs";
			}

			System.out.println("[*] Dumping npcs to: " + npcdir);
			dumpNpcs(store, new File(npcdir));
		}
		else if (cmd.hasOption("objects"))
		{
			String objectdir;
			objectdir = cmd.getOptionValue("objects");

			if (objectdir == null)
			{
				objectdir = "dumped-objects";
			}

			System.out.println("[*] Dumping objects to: " + objectdir);
			dumpObjects(store, new File(objectdir));
		}
		else if (cmd.hasOption("models"))
		{
			String modeldir;
			modeldir = cmd.getOptionValue("models");

			if (modeldir == null)
			{
				modeldir = "dumped-models";
			}

			System.out.println("[*] Dumping models to: " + modeldir);
			dumpModels(store, new File(modeldir));
		}
		else
		{
			System.err.println("[*] Nothing to do. Provide a command line argument to do something...");
			System.exit(-1);
			return;
		}
		
		System.out.println("[*] Finished.");
	}

	private static Store loadStore(String cache) throws IOException
	{
		Store store = new Store(new File(cache));
		store.load();
		return store;
	}

	private static Store loadStoreFlat(String cache) throws IOException
	{
		FlatStorage fs = new FlatStorage(new File(cache));
		Store store = new Store(fs);
		store.load();
		return store;
	}

	private static void dumpItems(Store store, File itemdir) throws IOException
	{
		ItemManager dumper = new ItemManager(store);
		dumper.load();
		dumper.export(itemdir);
		dumper.java(itemdir);
	}

	private static void dumpNpcs(Store store, File npcdir) throws IOException
	{
		NpcManager dumper = new NpcManager(store);
		dumper.load();
		dumper.dump(npcdir);
		dumper.java(npcdir);
	}

	private static void dumpObjects(Store store, File objectdir) throws IOException
	{
		ObjectManager dumper = new ObjectManager(store);
		dumper.load();
		dumper.dump(objectdir);
		dumper.java(objectdir);
	}

	private static void dumpModels(Store store, File modelDir) throws IOException
	{
		modelDir.mkdirs();
		Storage storage = store.getStorage();
		Index index = store.getIndex(IndexType.MODELS);

		for (Archive archive : index.getArchives())
		{
			int modelIndex = archive.getArchiveId();

			byte[] contents = archive.decompress(storage.loadArchive(archive));

			TextureManager tm = new TextureManager(store);
			tm.load();

			ModelLoader loader = new ModelLoader();

			ModelDefinition model;
			try
			{
				model = loader.load(archive.getArchiveId(), contents);
			}
			catch (NullPointerException ex)
			{
				System.err.println("[*] Error extracting models: " + ex.getMessage());
				System.out.println("[*] Try using the default flatcache provided with the project");
				System.exit(-1);
				return;
			}

			ObjExporter exporter = new ObjExporter(tm, model);

			String objFileOut = modelDir + File.separator + modelIndex + ".obj";
			String mtlFileOut = modelDir + File.separator + modelIndex + ".mtl";

			try (PrintWriter objWriter = new PrintWriter(new FileWriter(new File(objFileOut)));
			PrintWriter mtlWriter = new PrintWriter(new FileWriter(new File(mtlFileOut))))
			{
				exporter.export(objWriter, mtlWriter);
			}
		}
	}
}
