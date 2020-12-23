Chisel Project DFC Module
=======================

> More and more users are finding IntelliJ to be a powerful tool for Chisel coding. See the 
[IntelliJ Installation Guide](https://github.com/ucb-bar/chisel-template/wiki/IntelliJ-Installation-Guide) for how to install it.

### DFC Module
This is the repo of DFC hardware module

package Dfc

### How to get started
```sh
mkdir ~/ChiselProjects
cd ~/ChiselProjects

git clone https://github.com/ucb-bar/chisel-template.git MyChiselProject
cd MyChiselProject
```
### Make your project into a fresh git repo
There may be more elegant way to do it, but the following works for me. **Note:** this project comes with a magnificent 339 line (at this writing) .gitignore file.
 You may want to edit that first in case we missed something, whack away at it, or start it from scratch.
 
#### Clear out the old git stuff
```sh
rm -rf .git
git init
git add .gitignore *
```

#### Rename project in build.sbt file
Use your favorite text editor to change the first line of the **build.sbt** file
(it ships as ```name := "chisel-module-template"```) to correspond 
to your project.<br/>
Perhaps as ```name := "my-chisel-project"```

#### Clean up the README.md file
Again use you editor of choice to make the README specific to your project.
Be sure to update (or delete) the License section and add a LICENSE file of your own.

#### Commit your changes
```
git commit -m 'Starting MyChiselProject'
```
Connecting this up to github or some other remote host is an exercise left to the reader.

There are [instructions for generating Verilog](https://github.com/freechipsproject/chisel3/wiki/Frequently-Asked-Questions#get-me-verilog) on the Chisel wiki.

Some backends (verilator for example) produce VCD files by default, while other backends (firrtl and treadle) do not.
You can control the generation of VCD files with the `--generate-vcd-output` flag.

To run the simulation and generate a VCD output file regardless of the backend:
```bash
sbt 'test:runMain gcd.GCDMain --generate-vcd-output on'
```

To run the simulation and suppress the generation of a VCD output file:
```bash
sbt 'test:runMain gcd.GCDMain --generate-vcd-output off'
```

## Development/Bug Fixes
This is the release version of chisel-template. If you have bug fixes or
changes you would like to see incorporated in this repo, please checkout
the master branch and submit pull requests against it.

## License
This is free and unencumbered software released into the public domain.

Anyone is free to copy, modify, publish, use, compile, sell, or
distribute this software, either in source code form or as a compiled
binary, for any purpose, commercial or non-commercial, and by any
means.

In jurisdictions that recognize copyright laws, the author or authors
of this software dedicate any and all copyright interest in the
software to the public domain. We make this dedication for the benefit
of the public at large and to the detriment of our heirs and
successors. We intend this dedication to be an overt act of
relinquishment in perpetuity of all present and future rights to this
software under copyright law.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
IN NO EVENT SHALL THE AUTHORS BE LIABLE FOR ANY CLAIM, DAMAGES OR
OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE,
ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR
OTHER DEALINGS IN THE SOFTWARE.

For more information, please refer to <http://unlicense.org/>
