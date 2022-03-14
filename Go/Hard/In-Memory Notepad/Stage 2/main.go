package main

import (
	"bufio"
	"fmt"
	_ "fmt"
	"os"
	"strings"
	"unicode/utf8"
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

func addItem(list *[5]string, s string, i *int) {
	if *i == 5 {
		fmt.Println("[ERROR] Notepad is full!")
	} else if s != "" {
		list[*i] = s
		*i++
		fmt.Println("[OK] The note was successfully created")
	} else {
		fmt.Println("[ERROR] Sorry but no thanks!")
	}
}

func printNotepad(list [5]string) {
	for i := 0; i < 5; i++ {
		if list[i] == "" {
			break
		}
		fmt.Printf("[Info] %d: %s\n", i+1, list[i])
	}
}

func main() {
	var command = ""
	var scanner = bufio.NewScanner(os.Stdin)
	var list [5]string
	counter := 0
	for command != "exit" {
		fmt.Print("Enter command and data: ")

		scanner.Scan()

		val := scanner.Text()
		command = strings.Split(val, " ")[0]

		var text = ""
		if len(strings.Split(val, " ")) > 1 {
			text = val[utf8.RuneCountInString(command)+1:]
		}

		//fmt.Println(list)

		switch command {
		case "exit":
			fmt.Println("[INFO] BYE!")
		case "create":
			addItem(&list, text, &counter)
		case "list":
			printNotepad(list)
		case "clear":
			list = [5]string{}
			counter = 0
			fmt.Println("[Ok] All notes were successfully deleted")
		default:
			fmt.Println(command, val)
		}

	}
}
