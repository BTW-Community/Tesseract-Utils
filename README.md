# Tesseract Utils (BtW's Addon)

<p align="center">
  <img src="image/World Editor Banner1.png?raw=true" alt="Addon's banner" width="800"/>
</p>

# NOT UP TO DATE

This presentation is not up to date with versions past 0.8.0 !

Quick features explanation :
- you can access the building mode by pressing ALT and scrolling with your mouse to select the mode you want
- You cannot use most tools and other features while in a building mode beware ! It's not a bug !
- Each building mode have keybinds help on the bottom right corner, in most case your Sneak and Sprint key have special usage but not always pointed out
- commands are NOT similar to modern vanilla enter the first word of the command and enter to see what is needed: when <id> is asked you can use TAB to auto-fill with correct names or you can use the real id (+ the metadata if there is one, the number after the /)
- I advise to use F3+H to show block,items id and metadata if needed

## Presentation

This addon allow you to use handy tools such as setblock, copy/paste, move, replace, undo/redo, and much more.
Features inspired by WorldEdit.

It also has a bunch of QoL feature you can turn on and off as you want such as: custom reach, custom flying speed, disabling the flight momentum, disabling the placing cooldown, etc

**Tesseract Utils** give you new tools to play with to express your creativity like never before !

(These tools are only accessible in **creative**.)

## How to use

The wooden axe is being used as a selection tool. (A selection box is needed for // set, // replace, // move, // copy)

The wooden shovel is being used as a building tool. (place or replace an area with block in the form of a shape)

Each command start with a double "/" with space afterward. "<>" = required, "[]" = optional, "|" = or. 

When an id is asked you can either give it the block's id or its name (names can cause issue, use EMI and advanced tooltips (F3+H) to find the id of blocks)

When coordinates are asked (x,y,z), if you're looking at a block you can press "TAB" and it's going to enter that block's coordinate. By entering "\~\" it will use the corresponding coordinate of your current location, and by adding a number after the "\~\" it will offset the coordinate by the amount entered, example: // paste 23 ~ ~6, it will paste an object at x=23, y=your current Y coord and z=your Z coord + 6 (can be negative)

### Simple commands:

#### Give commands:

This addon improve the give command to allow more efficient usage.

- /give <id/meta\> [count\]

OR

- /give <block name\> [count\]

#### Summon commands:

This addon add the summon command to allow summoning entity in the world

- /summon <entity name\> [count\] [x\] [y\] [z\]

#### Kill commands:

This addon improve the killing command to allow more efficient usage.

- /kill <player|entity|item|all> <entity name\>

#### Inventory commands:

This addon allow you to save and load previously saved inventory.

- /inv <save|load|remove|preset> <name\>

#### Editing commands:

- "// setblock <ID\> <x\> <y\> <z\>" : Place a block at the designed coordinate with the ID given.


- "// set <ID\> [hollow|wall\] [thickness\]" : Replace a selection with blocks with the ID given. If shape specified: "hollow" create an empty box and "wall" create a wall, both with the thickness given (if not specified, default at 1).


- "// shape <ID\> <shape\> <parameters\> <x\> <y\> <z\> [hollow|open\] [replace\] " : Place a shape at the coordinate with the ID given, sphere/cylinder/cube are possible shapes, the parameters depends on which shape you use: sphere only needs one parameter= radius, the cylinder needs two= radius and height, and the cube needs three= sizeX, sizeY and sizeZ. These parameters are in the form of: 1\:1\:1. You can choose to place these shape hollowed or opened at the top (for the cylinder and cube shape), you can choose the thickness in this case by adding another value to the parameters (if i placed a full cylinder with a 5 radius and 3 height: parameters = 5\:3 , if place a hollow cylinder with same parameters but with a thickness of 2: parameters = 5\:3\:2)


- "// replace <ID\> [ID replaced\]" : Replace every block that are not air (if not specified) with the first ID. If second ID specified: replace only block matching with the second ID.


- "// move <to|add\> <x\> <y\> <z\>" : If "to": move a selection to designated coords (if not specified, move to the player's coords). If "add": move a selection to a direction by adding the coords specified.


- "// copy" : Copy a selection and store it temporarily.


- "// paste [x\] [y\] [z\]" : Paste the object stored to the designated coords (if not specified: paste at the player's coords) .


- "// undo [amount\]" : Revert an action if done with Tesseract Utils. If specified, revert "amount" of action.


- "// redo [amount\]" : Redo an action that was reverted.


- "// pos1" : Select the First point of the selection with the current coords of the player.


- "// pos2" : Select the Second point of the selection with the current coords of the player.


- "// tool <shape\> <id\> <parameters\> [replace\] [hollow|open\]" : Let you choose your building tool's caracteristics (for more information refer yourself to the shape command)

### Advanced commands:

You can add "ignoreAir" and/or "causeUpdate" as a parameter at the end of most command to either :

- "ignoreAir" = Ignore the air blocks. Example: "// copy ignoreAir", only actual blocks will be copied and not air, so when it gets pasted the empty blocks won't replace existing blocks.
- "causeUpdate" = Send an update to neighboring block when modifying/placing blocks with command (doesn't send updates by default).

#### Commands

- "// setblock <ID/meta\> <x\> <y\> <z\> [causeUpdate\] " 


- "// set <ID1/meta:%;ID2/meta:%;ID3/meta:%;...\> [hollow|wall\] [thickness\] [ignoreAir\] [causeUpdate\]"


- "// shape <x\> <y\> <z\> <ID1/meta:%;ID2/meta:%;ID3/meta:%;...\> <shape\> <radius\:thickness\:height\> [hollow\] 


- "// replace <ID1/meta:%;ID2/meta:%;ID3/meta:%;...\> [ID/meta replaced\] [causeUpdate\]"


- "// move <to|add\> <x\> <y\> <z\> [ignoreAir\] [causeUpdate\]"


- "// copy [ignoreAir\]"


- "// paste [x\] [y\] [z\] [ignoreAir\] [causeUpdate\]"

When an ID is asked, adding a "/" allow you to choose the block's metadata, example: cobblestone/0 (or 4/0) = strata 1 cobblestone,  cobblestone/1 (or 4/1) = strata 2 cobblestone.


When placing multiple block using // set or // replace:
- ";" can be added after an ID to add another block in the blocks added, example: // set stone;obsidian = will place stone and obsidian in the same proportion in the selection.
- ":" can be added after the ID to choose the previous ID proportion, example: // set stone:5;obsidian:15 = will place stone and obsidian with a ratio of 5/15 (1/3), meaning on average when 1 stone is placed, 3 obsidian is placed in the selection. 

It is possible to use everything at once but need to follow this order: // set ID1/meta:%;ID2/meta:%;ID3/meta:%;etc. 

Example: // set 1/2:75;obsidian:5;leaves/2:20 = will place 75% strata 2 stone, 5% obsidian, 20% birch leaves in the selection

### Quality of life additions:

- By pressing "F3"+"F4" and pressing multiple time F4 (while F3 is still pressed) you can cycle through the gamemode swap menu to choose your gamemode (cheats need to be ON)

- By pressing "H" (or modified keybind) your hotbar will cycle through your inventory rows.

- Chat limit increased to 512 characters (instead of 100).

- Addition of the "entity eraser" item that allow you to delete entity by clicking on it from any distance.

#### Quality of life configs: 

You can have access to the addon's config menu by either: going in the main game menu on the right of the "quit game" button, or in the ingame menu next to "open to lan", or directly ingame by pressing "F6" (or the keybind you chosed to bind). In this config menu, you can customize your game by activating/deactivating, selecting values for these configs.

- reach : let you decide your reach in creative mode.
  
- flySpeed : When pressing your sprint keybind, your fly speed will get set to that value.
  
- disablePlaceCooldown : disabled your player's placing cooldown.
  
- disableBreakCooldown : disable your player's breaking cooldown (instamine in creative).
  
- disableMomentum : disable your player's flight momentum = doesn't "slide" in the air.
  
- enableClickReplace : if enabled, right-clicking a block will replace it with the block in your hand (if sneaking, blocks will be placed normally).
  
- enableNoClip : if enabled, the player will pass through block like it's air, but can still interact with the world.

- extraDebugInfo : if enabled, coordinates/biome type/facing direction/light level/targeted block information will be shown in the F3 overlay.

## License

This addon is under the CC-BY 4.0 license.
(https://creativecommons.org/licenses/by/4.0/deed.en)
