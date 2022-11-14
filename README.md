# PrefabConverter
Ever had a prefab that works for one project, and when copying that prefab over to another project which SHOULD have all the content (Scripts, other object references, etc.) 
on that prefab, it just doesnt reassign all the references? (Because thats not really how that works but whatever) 

Well with this program, you can generate a neat little folder which
contains a converted prefab with correct references and all of its directly related assets! That includes models, sound files, and other things that were present in the
prefabs source project but not in the destination one.

# Setup
1) Download this program and make sure to put it in its own empty folder.
2) Make a .bat file in that same folder to run the program, run text is below.
```
java -jar PrefabConverter-1.0-SNAPSHOT.jar
```

# How To Use
When running the program, it will ask you for three things.
1) The filepath of the prefab you want to convert.
2) The filepath of the assets folder the prefab CAME FROM.
3) The filepath of the asssets folder you want the prefab to be CONVERTED TO.

# Usecases
This was mainly made for BONELAB modding to convert prefab files from rips of the game to extended SDK friendly prefabs. (So people can have access to these things without needing an entire rip of the game + shorter build times)

Im sure there are more instances where you would need to make a prefab support another project when the .meta files dont match between projects. 

Go nuts! Do alot!
Make things for people! Make things more accessible! It benefits the entire modding community.