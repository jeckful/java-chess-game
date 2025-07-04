SRC_DIR=src
BIN_DIR=bin
MAIN_CLASS=Main
CONSOLE_CLASS=ConsoleChessGame


SOURCES := $(shell find $(SRC_DIR) -name "*.java")
CLASSES := $(SOURCES:$(SRC_DIR)/%.java=$(BIN_DIR)/%.class)

.PHONY: run clean

all:
	javac -d $(BIN_DIR) $(SOURCES)
	cp -r Images $(BIN_DIR)

run: all
ifeq ($(MODE),console)
	java -cp $(BIN_DIR) $(CONSOLE_CLASS)
else
	java -cp $(BIN_DIR) $(MAIN_CLASS)
endif



clean:
	rm -rf $(BIN_DIR)
