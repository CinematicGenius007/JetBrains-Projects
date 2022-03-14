package main

import (
	"bufio"
	"fmt"
	_ "fmt"
	"os"
	"strings"
	//"unicode/utf8"
)

// global notepad variable
var notepad []string

func addItem(newElement string) {
	if len(strings.Split(newElement, " ")) <= 1 {
		fmt.Println("[Error] Missing note argument")
	} else if len(notepad) == cap(notepad) {
		fmt.Println("[ERROR] Notepad is full")
	} else if newElement != "" {
		notepad = append(notepad, newElement[7:])
		fmt.Println("[OK] The note was successfully created")
	} else {
		fmt.Println("[ERROR] Sorry but no thanks")
	}
}

func printNotepad() {
	if len(notepad) == 0 {
		fmt.Println("[Info] Notepad is empty")
	} else {
		for i := 0; i < len(notepad); i++ {
			fmt.Printf("[Info] %d: %s\n", i+1, notepad[i])
		}
	}
}

func updateNotepad(request string) {
	if len(notepad) == 0 {
		fmt.Println("[Error] There is nothing to update")
	} else if len(strings.Split(request, " ")) <= 1 {
		fmt.Println("[Error] Missing position argument")
	} else {
		indexString := strings.Split(request, " ")[1]
		var index int
		_, scan := fmt.Sscan(indexString, &index)
		if scan != nil {
			fmt.Printf("[Error] Invalid position: %s\n", indexString)
		} else if index-1 >= len(notepad) {
			fmt.Printf("[Error] Position %d is out of the boundaries [1, %d]\n", index, cap(notepad))
		} else if len(strings.Split(request, " ")) <= 2 {
			fmt.Println("[Error] Missing note argument")
		} else {
			val := strings.Join(strings.Split(request, " ")[2:], " ")
			notepad[index-1] = val
			fmt.Printf("[OK] The note at position %d was successfully updated\n", index)
		}
	}
}

func deleteItem(request string) {
	if len(notepad) == 0 {
		fmt.Println("[Error] There is nothing to deleted")
	} else if len(strings.Split(request, " ")) <= 1 {
		fmt.Println("[Error] Missing position argument")
	} else {
		indexString := strings.Split(request, " ")[1]
		var index int
		_, scan := fmt.Sscan(indexString, &index)
		if scan != nil {
			fmt.Printf("[Error] Invalid position: %s\n", indexString)
		} else if index-1 >= len(notepad) {
			fmt.Printf("[Error] Position %d is out of the boundaries [1, %d]\n", index, cap(notepad))
		} else {
			firstHalf, secondHalf := notepad[:index-1], notepad[index:]
			notepad = make([]string, 0, cap(notepad))

			for i := 0; i < len(firstHalf); i++ {
				notepad = append(notepad, firstHalf[i])
			}

			for i := 0; i < len(secondHalf); i++ {
				notepad = append(notepad, secondHalf[i])
			}

			fmt.Printf("[OK] The note at position %d was successfully deleted\n", index)
		}
	}
}

func main() {
	var command = ""
	var scanner = bufio.NewScanner(os.Stdin)

	var capacity int
	fmt.Print("Enter the maximum number of notes: ")
	_, err := fmt.Scan(&capacity)
	if err != nil {
		return
	}

	notepad = make([]string, 0, capacity)

	for command != "exit" {
		fmt.Print("Enter command and data: ")

		scanner.Scan()
		val := strings.Trim(scanner.Text(), " ")
		command = strings.Split(val, " ")[0]

		switch command {
		case "exit":
			fmt.Println("[INFO] BYE!")
		case "create":
			addItem(val)
		case "update":
			updateNotepad(val)
		case "list":
			printNotepad()
		case "delete":
			deleteItem(val)
		case "clear":
			notepad = make([]string, 0, capacity)
			fmt.Println("[Ok] All notes were successfully deleted")
		default:
			fmt.Println("[Error] Unknown command")
		}
	}
}
