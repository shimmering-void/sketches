# sketches

A collection of generative art sketches using [`clojure`](https://clojure.org/), [`quil`](http://www.quil.info/) and [`figwheel-main`](https://figwheel.org/).

Based off the [`figwheel-main-template`](https://github.com/bhauman/figwheel-main-template) for `leiningen`.

## Gallery

### perlin-flow

Inspired by https://functional.christmas/2019/13.

![](https://raw.githubusercontent.com/bfollington/sketches/main/renders/city-smoke.png)

### function-plot

Inspired by https://www.youtube.com/watch?v=spUNpyF58BY.

![](https://raw.githubusercontent.com/bfollington/sketches/main/renders/function-plot.png)

### dragon-curve

Inspired by https://breaksome.tech/coding-a-dragon-curve-in-p5js/.

![](https://raw.githubusercontent.com/bfollington/sketches/main/renders/dragon-curve.png)

### cells

Inspired by https://mathworld.wolfram.com/ElementaryCellularAutomaton.html

![](https://raw.githubusercontent.com/bfollington/sketches/main/renders/cells.png)

### cells-2d

Inspired by https://content.wolfram.com/uploads/sites/34/2020/07/two-dimensional-cellular-automata.pdf

![](https://raw.githubusercontent.com/bfollington/sketches/main/renders/cells-2d.png)

### cells-2d-sprites

![](https://raw.githubusercontent.com/bfollington/sketches/main/renders/cells-2d-sprites.png)

## Development

To get an interactive development environment run:

    lein fig -b dev -r

This will auto compile and send all changes to the browser without the
need to reload. After the compilation process is complete, you will
get a Browser Connected REPL. An easy way to try it is:

    (js/alert "Am I connected?")

and you should see an alert in the browser window.

To clean all compiled files:

	lein clean

To create a production build run:

	lein clean
	lein fig:min


## License

Copyright © 2020 Ben Follington

Distributed under the Eclipse Public License either version 1.0 or (at your option) any later version.
