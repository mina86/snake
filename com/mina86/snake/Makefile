SOURCEFILES	= $(wildcard *.java)
CLASSFILES  = $(subst .java,.class,$(SOURCEFILES))
CLASSPATH	= $(abspath ../../../)

all: $(CLASSFILES)

%.class: %.java
	javac -classpath "$(CLASSPATH)" -Xlint $<

run: all
	java -classpath "$(CLASSPATH)" com.mina86.snake.Main

Snake.jar: all
	jar cvfm -C "$(CLASSPATH)" $@ com/mina86/snake/Manifest com/mina86/snake/*.class

doc: $(SOURCEFILES)
	rm -rf -- $@
	javadoc -private -encoding UTF-8 -d doc -use -version -author \
	-windowtitle 'Snake Documentation' -charset UTF-8 \
	-linksource -sourcetab 4 -keywords -docencoding UTF-8 -splitindex *.java
	[ -d $@/com/mina86/snake/doc-files ] || cp -R -- doc-files $@/com/mina86/snake/doc-files
