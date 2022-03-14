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

func addItem(list []string, s string, i *int) {
	//fmt.Println(len(list))
	if *i == len(list) {
		fmt.Println("[ERROR] Notepad is full")
	} else if s != "" {
		list[*i] = s
		*i++
		fmt.Println("[OK] The note was successfully created")
	} else {
		fmt.Println("[ERROR] Sorry but no thanks")
	}
}

func printNotepad(list []string) {
	for i := 0; i < len(list); i++ {
		if list[i] == "" {
			break
		}
		fmt.Printf("[Info] %d: %s\n", i+1, list[i])
	}
}

func main() {
	var command = ""
	var scanner = bufio.NewScanner(os.Stdin)

	var length int
	fmt.Print("Enter the maximum number of notes: ")
	_, err := fmt.Scan(&length)
	if err != nil {
		return
	}

	var list = make([]string, length)
	counter := 0
	for command != "exit" {
		fmt.Print("Enter command and data: ")

		scanner.Scan()
		val := scanner.Text()
		command = strings.Split(val, " ")[0]

		var text = ""
		if len(strings.Split(strings.Trim(val, " "), " ")) > 1 {
			text = val[utf8.RuneCountInString(command)+1:]
		}

		switch command {
		case "exit":
			fmt.Println("[INFO] BYE!")
		case "create":
			if text != "" {
				addItem(list, text, &counter)
			} else {
				fmt.Println("[Error] Missing note argument")
			}
		case "list":
			if counter == 0 {
				fmt.Println("[Info] Notepad is empty")
			} else {
				printNotepad(list)
			}
		case "clear":
			list = make([]string, length)
			counter = 0
			fmt.Println("[Ok] All notes were successfully deleted")
		default:
			fmt.Println("[Error] Unknown command")
		}

	}
}
