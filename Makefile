SOURCEFILES = $(wildcard com/mina86/snake/*.java)
CLASSFILES  = $(subst .java,.class,$(SOURCEFILES))
CLASSPATH   = $(PWD)

all: $(CLASSFILES)

com/mina86/snake/%.class: com/mina86/snake/%.java
	exec javac -classpath . -Xlint $<

run: all
	exec java -classpath . com.mina86.snake.Main

Snake.jar: all
	exec jar cvfm $@ Manifest com/mina86/snake/*.class

doc: $(SOURCEFILES)
	exec rm -rf -- $@
	exec javadoc -private -encoding UTF-8 -d doc -use -version -author \
	-windowtitle 'Snake Documentation' -charset UTF-8 \
	-linksource -sourcetab 4 -keywords -docencoding UTF-8 -splitindex com/mina86/snake/*.java
	[ -d $@/com/mina86/snake/doc-files ] || cp -R -- doc-files $@/com/mina86/snake/doc-files

clean::
	exec rm -f -- com/mina86/snake/*.class
	exec rm -rf -- doc
