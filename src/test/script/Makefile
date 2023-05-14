ASS_FILES := $(shell find ../deca/codegen/ -name '*.ass')

all: test

test: $(ASS_FILES)

%.ass: %.deca
	@echo "running $^"
	-@if [ -f $@ ]; then \
		./testima.sh $^; \
	fi

clean:
	@find ../deca -name "*.ass" -print -delete
	@echo "removed .ass files"

.PHONY: clean
