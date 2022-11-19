# PrefabConverter
THIS PROJECT WAS MADE WITH JAVA 17, IT WILL NOT RUN IF YOU HAVE A VERSION OF JAVA LOWER THAN JAVA 17+, YOU CAN DOWNLOAD JAVA 17 HERE: https://www.oracle.com/java/technologies/downloads/#jdk17-windows


Ever had a prefab that works for one project, and when copying that prefab over to another project which SHOULD have all the content (Scripts, other object references, etc.) 
on that prefab, it just doesnt reassign all the references? (Because thats not really how that works but whatever) 

Well with this program, you can generate a neat little folder which
contains a converted prefab with correct references and all of its directly related assets! That includes models, sound files, and other things that were present in the
prefabs source project but not in the destination one.

# Setup
Download the zip from releases and run the run.bat

# How To Use
When running the program, it will ask you for three things.
1) The filepath of the prefab you want to convert.
2) The filepath of the assets folder the prefab CAME FROM.
3) The filepath of the asssets folder you want the prefab to be CONVERTED TO.

Wait for it to finish and parse all the .meta files and find all appropriate references. Might take a while depending on the size of either projects.
Although, if you choose to run the program again after it completes, it will read from memory. Meaning if you wanna convert multiple prefabs from the same origin project/destination project it should be almost immediate.

When its done, itll create a folder called "FinalConversion" in the same directory the jar is in. You can rename that to whatever and drag that into your destination project and it should work.

# Usecases
This was mainly made for BONELAB modding to convert prefab files from rips of the game to extended SDK friendly prefabs. (So people can have access to these things without needing an entire rip of the game + shorter build times)

Im sure there are more instances where you would need to make a prefab support another project when the .meta files dont match between projects. 

Go nuts! Do alot!
Make things for people! Make things more accessible! It benefits the entire modding community.

# PRs or Contributions
There are alot of things im sure can be made better/more features that can be added, this was made fairly quickly just cause I was annoyed. PRs are welcome!
