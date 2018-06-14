
SRCDIR=src
BINDIR=bin

MAINCLASS=parser.Parser

##########################################################
JAVA_FILES = $(shell find $(SRCDIR)/ -type f -name '*.java')
BIN_FILES = $(subst $(SRCDIR), $(BINDIR), $(JAVA_FILES:.java=.class))
#$(patsubst $(JAVA_FILES), $(BINDIR)/%.javac, $(JAVA_FILES))

all: images bindir_exists $(BIN_FILES)
	$(echo $(BIN_FILES))

bindir_exists:
	mkdir -p $(BINDIR)

images:
	mkdir -p images

$(BIN_FILES): $(JAVA_FILES)
	javac -classpath $(BINDIR) $^ -d $(BINDIR)

run: all
	java -classpath $(BINDIR) $(MAINCLASS)

#%.class: $(subst $(BINDIR), $(SRCDIR), %.java)
#	javac $<

clean:
	rm -rf $(BINDIR)
	rm -f $(BIN_FILES)
