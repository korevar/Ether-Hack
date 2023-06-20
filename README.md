# Project-Zomboid-EtherHack
This is a cheat written in Java for Project Zomboid. It is aimed at providing the game with additional functionality that allows users to get some benefits. Please use responsibly and understand the consequences that may arise as a result of improper use.
The performance of the cheat was tested on the latest version of the game `41.78` (November 11, 2022).
## Table of Contents
- [Cheat functionality]( #cheat-functionality)
- [Getting Started](#getting-started)
- [Prerequisites](#prerequisites)
- [Installation](#installation)
- [Usage](#usage)
- [Contributing](#contributing)
- [Disclaimer](#disclaimer)
- [License](#license)
- [Contact](#contact)

## Cheat functionality

| Function         | Working in multiplayer | Working in a co-op | Description                                                                                                                                            |
|------------------|:----------------------:|:------------------:|--------------------------------------------------------------------------------------------------------------------------------------------------------|
| Skill            |           +            |         +          | Sets all character skills to the maximum (10) level                                                                                                    |
| Noclip           |           -            |         +          | Allows you to pass through walls and other objects                                                                                                     |
| GodMode          |           -            |         +          | Gives invulnerability to the character                                                                                                                 |
| Ghost            |           -            |         +          | Gives invisibility to the character                                                                                                                    |
| Weight           |           -            |         +          | Increases the maximum weight of the inventory (however, there is a problem - you cannot move things to the inventory if its volume is more than 50 kg) |
| Get Admin Access |           -            |         +          | Grants admin access rights                                                                                                                             |
| Create item      |           +            |         +          | Allows you to create things by [Item ID](https://pzwiki.net/wiki/Items)                                                                                |

## Getting Started

This section will provide information on how to get a local copy of the project up and running.

### Prerequisites

This tool requires:
- [Java Version 8 Update 371](https://www.java.com/en/download/) or newer
- Steam copy of [Project Zomboid](https://store.steampowered.com/app/108600/Project_Zomboid/)

### Installation

1. Download and install Java on your computer
2. Make sure that the path to Java is set in your environment variables (For Windows: `WIN + X -> "System" -> "Advanced System Parameters" -> "Environment Variables" -> Double click on "Path" -> Insert path to java, for example "C:\Program Files\Java\jre-1.8\bin"`)
3. Clone the repository
4. Open the project in the IDE and build the executable via Gradle `.jar` file
5. Move the created `.jar` to the root folder of the game (For example, `c:\Steam\steamapps\common\ProjectZomboid`)
6. Open the console in the root folder and run the following command: `java -jar ./EtherHack-0.0.1a.jar --install`

## Usage

Run the `.jar` executable file in the root folder of the game with the `--install` flag and after installation you can run the game

## Contributing

We welcome contributions from the community. If you want to contribute, please fork the repository and create a pull request with your changes.

## Disclaimer

This software is provided 'as-is', without any express or implied warranty. In no event will the author be held liable for any damages arising from the use of this software. Use of this software may also violate the terms of service of the game and could lead to your account being banned. Use at your own risk.

## License

This project is under `MIT License` - see the LICENSE file for details.

## Contact

If you have any questions, feel free to reach out to me at `rubranny@gmail.com`
