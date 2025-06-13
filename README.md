# Tesseract Utils (BtW's Addon)

<p align="center">
  <img src="image/World Editor Banner1.png?raw=true" alt="Addon's banner" width="800"/>
</p>

## /!\ NOT UP TO DATE /!\

## Presentation

This addon allow you to use handy tools such as setblock, copy/paste, move, replace, undo/redo, and much more.
Features inspired by WorldEdit.

It also has a bunch of QoL feature you can turn on and off as you want such as: custom reach, custom flying speed, disabling the flight momentum, disabling the placing cooldown, etc

**Tesseract Utils** give you new tools to play with to express your creativity like never before !

(These tools are only accessible in **creative**.)

## How to use

The wooden axe is being used as a selection tool. (A selection box is needed for // set, // replace, // move, // copy)

Each command start with a double "/" with space afterward. "<>" = required, "[]" = optional, "|" = or. 

When an id is asked you can either give it the block's id or its name (names can cause issue, use EMI and advanced tooltips (F3+H) to find the id of blocks)

When coordinates are asked (x,y,z), if you're looking at a block you can press "TAB" and it's going to enter that block's coordinate. By entering "\~\" it will use the corresponding coordinate of your current location, and by adding a number after the "\~\" it will offset the coordinate by the amount entered, example: // paste 23 ~ ~6, it will paste an object at x=23, y=your current Y coord and z=your Z coord + 6 (can be negative)

This addon is, for the time being, almost only usable by entering commands, here is the list and explanation of each command:

### Simple commands:

#### Editing commands:

- "// setblock <x\> <y\> <z\> <ID\>" : Place a block at the designed coordinate with the ID given.


- "// set <ID\> [hollow|wall\] [thickness\]" : Replace a selection with blocks with the ID given. If shape specified: "hollow" create an empty box and "wall" create a wall, both with the thickness given (if not specified, default at 1).


- "// replace <ID\> [ID replaced\]" : Replace every block that are not air (if not specified) with the first ID. If second ID specified: replace only block matching with the second ID.


- "// move <to|add\> <x\> <y\> <z\>" : If "to": move a selection to designated coords (if not specified, move to the player's coords). If "add": move a selection to a direction by adding the coords specified.


- "// copy" : Copy a selection and store it temporarily.


- "// paste [x\] [y\] [z\]" : Paste the object stored to the designated coords (if not specified: paste at the player's coords) .


- "// undo [amount\]" : Revert an action if done with Tesseract Utils. If specified, revert "amount" of action.


- "// redo [amount\]" : Redo an action that was reverted.


- "// pos1" : Select the First point of the selection with the current coords of the player.


- "// pos2" : Select the Second point of the selection with the current coords of the player.

#### Quality of life commands:

- "// reach <block amount\>" : self-explanatory. (default: 5)


- "// flySpeed <speed modifier\>" : When pressing your sprint keybind, your fly speed get set to that value. (default: 1)


- "// disablePlaceCooldown <true|false\>" : self-explanatory. (default: false)


- "// disableBreakCooldown <true|false\>" : self-explanatory. (default: false)


- "// disableMomentum <true|false\>" : disable your player's flight momentum = doesn't "slide" in the air. (default: false)


- "// enableClickReplace <true|false>" : if enabled, right-clicking a block will replace it with the block in your hand (if sneaking, blocks will be placed normally). (default: false)


- "// enableNoClip <true|false> : if enabled, the player will pass through block like it's air, but can still interact with the world. (default: false)"

(All QoL feature are accessible through configs as well, beware if changed the modifications will be directly applied when joining the game)

### Advanced commands:

You can add "ignoreAir" and/or "causeUpdate" as a parameter at the end of most command to either :

- "ignoreAir" = Ignore the air blocks. Example: "// copy ignoreAir", only actual blocks will be copied and not air, so when it gets pasted the empty blocks won't replace existing blocks.
- "causeUpdate" = Send an update to neighboring block when modifying/placing blocks with command (doesn't send updates by default).

#### Commands

- "// setblock <x\> <y\> <z\> <ID/meta\> [causeUpdate\] " 


- "// set <ID1/meta:%;ID2/meta:%;ID3/meta:%;...\> [hollow|wall\] [thickness\] [ignoreAir\] [causeUpdate\]"


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

## License

This addon is under the CC-BY 4.0 license.
(https://creativecommons.org/licenses/by/4.0/deed.en)
