# squirrel

Climbing trees. Eating nuts. Parsing cache. Like the humble squirrel, this is a humble tool. Designed to extract some useful, interesting data from the OSRS cache. I use this in my OSRS projects such as [`osrsbox-db`](https://github.com/osrsbox/osrsbox-db) - but you can use it for whatever you want. Powered by the awesome [RuneLite Cache package](https://github.com/runelite/runelite/tree/master/cache). Also leverages the excellent [`osrs-cache` repo by Abex](https://github.com/abextm/osrs-cache) - which hosts an up-to-date version of the OSRS cache - with historic versions to boot.

This tool has the functionality to dump the following data from the cache:

- ItemDefinitions in JSON format
- NpcDefinitions in JSON format
- ObjectDefinitions in JSON format
- Models in OBJ + MTL formats

FYI - the RuneLite cache tool does all the hard lifting, this is not a replacement, nor am I trying to steal their thunder... Probably not even possible - that project is **too good**! This is just an easier way for me to run some automated stuff and a random way to try to keep upskilling in Java. Shamelessly ~~copied~~ molded some RuneLite code for inspiration - but the original BSD-2 license is retained.

## Quickstart

This project repo aims to be ready to go - batteries included. So the compiled JAR file is shipped with the repo, as well as a tested/verified latest version of the OSRS cache (as a Git submodule). If you want to make life difficult - use your version of the cache. If you want to make life easy - use the `osrs-cache` data provided with this repo. Here are some hopefully useful quickstart steps:

- Requirements:
    - Make sure you have Java installed (at least JRE)
    - Make sure you have Git installed
- Checkout GitHub repo with `recursive` option to get `osrs-cache` submodule (**warning**: the submodule repo is a decently sized download - approximately 360MiB)

```
git clone --recursive https://github.com/osrsbox/squirrel.git
```

- Use the prebuilt binary (JAR) and bundled `osrs-cache` repo to do stuff...
- For example, to dump ItemDefinition data in JSON format to the default folder named `dumped-items`, try the following:

```
java -jar squirrel.jar --items
```

- Another example, dump models in OBJ + MTL formats to the default folder named `dumped-models`:

```
java -jar squirrel.jar --models
```

## Updating Project

Just pull to get the newest version of the repo:

```
git pull
```

Will increment the project version when there are changes. You can check the version with:

```
java -jar squirrel.jar -v
```

If you want to update the `osrs-cache` submodule (which I will probably not constantly update after each weekly update) just use git. For example, in the root folder run:

```
git submodule update --init
```

## Using a Specific Cache Version

Abex's [`osrs-cache` repo](https://github.com/abextm/osrs-cache) is updated constantly - but it also hosts historical versions of the cache. This is good if you want to checkout any data that has been removed since it was originally released. A good example is holiday event items that are removed when the event is over. The cache dates back to mid-2017. If you want to load up an older version - just use the power of git to checkout the appropriate commit.

To find commits of interest run:

```
git log --oneline
```

Then review the output. An example is provided below:

```
06d6313 (tag: 2020-01-16-rev182) Cache version 2020-01-16-rev182
4cec937 (tag: 2020-01-09-rev182) Cache version 2020-01-09-rev182
e2ccb30 (tag: 2019-12-16-rev182) Cache version 2019-12-16-rev182
7340b06 (tag: 2019-12-11-rev182) Cache version 2019-12-11-rev182
9200e14 (tag: 2019-12-06-rev182) Cache version 2019-12-06-rev182
1768ea1 (tag: 2019-12-05-rev182) Cache version 2019-12-05-rev182
```

Say you wanted to get item information from the Christmas event in 2019 - you could try checkout the commit with the `e2ccb30` hash value. To check this specific commit out, use:

```
git checkout e2ccb30
```

Then dump any data you want using the tool... To switch back to the latest version (after you are done), use:

```
git checkout -
```

## Using Your Cache

Sometimes you may want to use a specific cache for a specific reason. For example, to dump data from a specific revision that is not available in Abex's cache repo. If you want to do that, just supply the location of your cache using the `--cache` argument, followed by the file system location.

For example, use your cache from the default location on Linux and dump the ItemDefinitions:

```
java -jar squirrel.jar --cache ~/jagexcache/oldschool/LIVE --items
```

Similarly on Windows (I think this should work - I don't have Windows to test):

```
java.exe -jar squirrel.jar --cache %USERPROFILE%\jagexcache\oldschool\LIVE --items
```

You can also use your cache [stored in the Flatpack format](https://github.com/runelite/runelite/blob/aff6ea6fa982eeed1de56066fb02e0d0f8b2e787/cache/src/main/java/net/runelite/cache/fs/flat/FlatStorage.java). Just use the `--flatcache` argument followed by the folder containing the data.

## Compiling The Project

This project is written in Java and uses Maven. So you will need the following dependencies to compile:

- Java Development Kit (JDK) 1.7 (Java 7)
- Maven

You can easily compile using Maven from the terminal:

```
mvn clean install -Dmaven.test.skip=true
```

The project configuration (`pom.xml` file) specifies for an uber-jar to be created with all dependencies - simply to try to make life easier and bundle all dependencies in one JAR. This uber-jar is saved to the root project folder, under the name `squirrel.jar` without any version number suffix.

## Security

If you are really worried. Check the source code, then compile yourself :)

If you are semi-worried. Check the MD5 or SHA1 hash values of the `squirrel.jar` file:

```
# Version 1.0 hashes
f9130a95882f18f566667c10353690bb  squirrel.jar
7508e720399489654c8d83b8e66336172ce92682  squirrel.jar

# Version 1.1 hashes
d993ed76e99300da9986f9212033ffa4  squirrel.jar
68a7aecfb9f6ca55d2552df8a4627a77e7e5876e  squirrel.jar
```

## Tests

Yeah... Nah. Have written a couple of unit tests to check loading the cache and parsing the models. These are useful for me to do some automated testing, but probably not for anyone else. They are not included in the repo.

## ProTips

- Use the `osrs-cache` submodule that is included. Seriously, it makes life way easier and is what I use and test against.
- If you get the following error: `[*] Loading default flatcache from: null` - you probably didn't use `--recursive` when checking out the GitHub repo, and the needed submodule is not available! The easiest way to fix this is to delete the squirrel repo folder and try again. Or pull down the submodule if you have some foo.
- If you are on Windows you will need to run `java.exe` instead of just `java`. FYI - all examples in this readme are from a Linux system so just use `git` and `java` without the executable extension.
- Make sure you are in the root project directory when running the provided commands. Make sure you can `ls` or `dir` and see the `squirrel.jar` file.
- If you get some Java "path" error - this means that the `java.exe` (Windows) or `java` (Linux/OS X) command cannot be automatically found on your system. Try using Google to help figure out how to fix this.
- If you don't supply an argument for either `--items`, `--npcs`, `--objects`, or `--models` - the default folder of `dumped-<type>` will be used. For example, `dumper-items` when you dump ItemDefinitions.

## Contribution

Yeah - sure :) Happy to have any contributions. This repo doesn't have any constraints and rules - apart from being a nice human. More than happy for PRs that fix my terrible code or design issues - or that add functionality.

## License and Other Stuff

Since this project uses the RuneLite Cache package, it retains the same license: [BSD 2-Clause License](./LICENSE). Many thanks to all RuneLite contributors that help make it an awesome piece of software. Shoutout to Adam who manages it faithfully. And a final shoutout to Abex for writing some awesome software, and managing some super useful repos.

This project is for educational purposes. Please do not use it for nefarious purposes and/or personal gain. Be a cool human. Please don't PM me about private servers or hacking tools - not interested at all. This project was written on a Sunday morning with 1 hangover, 3 coffees, then 4 beers. Results may vary.

Old School RuneScape (OSRS) content and materials are trademarks and copyrights of JaGeX or its licensors. All rights reserved. OSRSBox and this little squirrel project are not associated or affiliated with JaGeX or its licensors.

## Why Squirrel?

Why not?
