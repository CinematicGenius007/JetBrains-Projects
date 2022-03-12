package main

import (
	"fmt"
	_ "fmt"
)

// One-line comment

// Multiline comments
//               ▀▀█
//   ▄▄▄   ▄▄▄     █     ▄▄▄   ▄ ▄▄    ▄▄▄▄
// █▀ ▀█  █▀ ▀█    █    ▀   █  █▀  █  █▀ ▀█
// █   █  █   █    █    ▄▀▀▀█  █   █  █   █
// ▀█▄▀█  ▀█▄█▀    ▀▄▄  ▀▄▄▀█  █   █  ▀█▄▀█
// ▄  █                               ▄  █
// ▀▀                                 ▀▀
//

/*
This is a typical block comment
*/

/*
< this is also a block comment >
----------------------------------
        \   ^__^
         \  (oo)\_______
            (__)\       )\/\
               ||----w |
               ||     ||
*/

func main() {
	var command = ""
	for command != "exit" {
		fmt.Print("Enter command and data: ")

		_, err := fmt.Scan(&command)
		if err != nil {
			return
		}

		switch command {
		case "exit":
			fmt.Println("[INFO] BYE!")
		default:
			fmt.Println(command)
		}

	}
}
