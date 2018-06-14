# graphics-engine-java


If your first reaction is

## Ewwwwwww, Java?

Then to heck with you I say, I rewrote my C++ engine in Java. It took like one day, and I don't regret a darn thing.

To everyone else,

**Welcome!** This is Adris Jautakas's Graphics engine.


## Features List:

Here's a list of extra features that make this one unique:

- Unique and (Mostly) well organized structure. Except for the bleeding edge stuff inside of the Renderer and TriangleBuffer, most of my code is written from
the ground up, and is structured in a concise (albeit sometimes inconvenient) way.
- Custom-ish parser which you probably don't want to see, but hey, it exists!
- Texture mapping! In theory, this engine should be able to map any texture to any shape, but I haven't implemented obj file loading so you'll have to stick with boring cubes. The engine has a way to render cube textures in its parser
- A few extra parser commands that I thought were convenient to add


## Extra Commands:

Here are the extra commands that were added:

- **BASESIZE** `width height`: Sets the size of the image. Example: `BASESIZE 400 400` for a 400x400 image
- **BACKGROUND** `r g b`: Sets the background color of the image. Example: `BACKGROUND 255 255 255` for a white background
- **BOX** `x y z width height length <image>`: Box, but with the option to map a square texture to it. For example: `BOX 0 0 0 100 100 100 res/test.png` maps `res/test.png` to a 100x100x100 box placed at the origin.

## BUGS:

- The entire engine

(just kidding)

**REAL BUGS:**

- For some reason, when rendering a sphere without any rotation, the upper triangles in the middle don't render. It appears as if whenever there's a triangle with a flat top, it doesn't render. I even copied the scanline code from dwsource to no avail.

- Texture mapping is wonky, but semi-functional. You can see from my gallery submission that in some situations, texture mapping works, but in most other situations it totally doesn't. It might have something to do with the way I determine the texture vertices. I'll have to look into that.

- When generating gifs and overriting them, the folder to store the gif files is not deleted. This means that when generating a gif that has less frames but the same name as another gif, they will overlap once the first gif is done. This could be fixed by deleting all files in the folder before compiling the gif files, or by compiling them with a limit in animation/Animation.java.

## User Instructions:

You should be able to just run

`make run`

and the engine will run the script file found in `src/script`.

TO RUN OTHER SCRIPT FILES:

`make ARGS="<script file goes here>" run`

For instance

`make ARGS="scr/scriptKnobSphere" run`

TO VIEW THE IMAGE YOU CREATED:

`animate <image goes here`

For instance

`animate images/lmao.gif`
