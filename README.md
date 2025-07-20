# SampleDynamicNBackApp


This repo demonstrates the use of fairly standard Android design patterns to create the mental equivalent of a fidget toy.  It is a version of the working memory brain training game n-back.  The app is called Dynamic N-Back because it allows the user to adjust the load factor (referred to in the source code with the variable name 'N') in- game while also adjusting the amount of time that the stimulus is presented before the trial ends.  It also allows the user to adjust the delay between trials where no stimulus is shown.

##### The Game

[Studies have shown](https://en.wikipedia.org/wiki/N-back) that n-back training can increase fluid intelligence.   This app aims to family the process where the player can manually adjust the difficulty of the task to reach the sweet spot where they are in flow state.  Cognitively, I have personally found that playing this game puts me into a state of alertness and focus that can carry on for minutes after I put the game down.In dynamic n-back, you can increase the load factor dynamically, by moving a slider.  For users with disabilities unrelated to their working memory, Dynamic N-Back allows the player to adjust the duration in time that the stimulus appears.  This is called the "impression time".  The app also allows you to adjust the amount of time between trials where no stimulus is shown.  This is the review period.  You can adjust these parameters to what makes you the most comfortable as you reach for higher scores.

##### Scoring

The scoring method measures roughly how much better the player is at correctly answering when the current stimulus is the same as the one n back and a reference player making random guesses.  For playability purposes, the probability distribution of the trials where the current stimulus is the one n back needs to occur much more frequently than it would be if stimuli are drawn randomly from the space of possibilities.  Scoring incorporates a "rubber-banding" parameter which increases the chance of a repeat more time that goes by without a repeat occuring.  This is to prevent player boredom of having to go for many trials without seeing a repeat and thus seeing a reward badge.

#### Privacy
This code doesn't require any special permissions nor does it use your data. 
##### Bugs and Planned improvements
 1. [ ]  Fix launcher icon to avoid clipping on certain devices
2. [ ] Add unit test for play game use case
3. [ ] Improve match declaration button on game screen
4. [ ] Show score history as a line-graph on game review screen
5. [ ] Add ability to set the number of trials in a game
6. [ ] Improve score increase badge
7. [ ] Add support for audio-visual badges
8. [ ] Add ability to remove audio badge config