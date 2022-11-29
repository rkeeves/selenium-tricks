# selenium-tricks

Tiny examples of interacting with Selenium.

## Overview

Selenium is a cool tool maintained and developed by volunteers for free.
It has a long history, and it is quite well-known in the industry.

This repository contains tiny usecases for different features of Selenium.
I tried to refrain from any complex abstractions.
Each test class is one self-contained unit.
No crazy design patterns, monads... just raw code for bare essentials.
In reality, you'd probably abstract all the duplicated code and lengthy interactions into some library.

Selenium uses WebDriver Protocol. You can do a lot with it, but you might be interested in CDP too.
This is why some examples use CDP.