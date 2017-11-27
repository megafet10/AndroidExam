/*
 * BORQS Software Solutions Pvt Ltd. CONFIDENTIAL
 * Copyright (c) 2017 All rights reserved.
 *
 * The source code contained or described herein and all documents
 * related to the source code ("Material") are owned by BORQS Software
 * Solutions Pvt Ltd. No part of the Material may be used,copied,
 * reproduced, modified, published, uploaded,posted, transmitted,
 * distributed, or disclosed in any way without BORQS Software
 * Solutions Pvt Ltd. prior written permission.
 *
 * No license under any patent, copyright, trade secret or other
 * intellectual property right is granted to or conferred upon you
 * by disclosure or delivery of the Materials, either expressly, by
 * implication, inducement, estoppel or otherwise. Any license
 * under such intellectual property rights must be express and
 * approved by BORQS Software Solutions Pvt Ltd. in writing.
 *
 */

#ifdef __cplusplus
extern "C" {
#endif

#define REVERIE_OK        0
#define REVERIE_ERROR    -1

/*Reverie Language Constants*/
typedef enum languageindex
{
    Hindi,
    Assamese,
    Bengali,
    Gujarati,
    Marathi,
    Telugu,
    Tamil,
    Malayalam,
    Punjabi,
    Odia,
    Kannada,
    Urdu,
    Kashmiri,
    English,
    Nepali,
    Konkani,
    Maithili,
    Dogri,
    Sindhi,
    Sanskrit,
    Manipuri,
    Bodo,
    Santali
} LanguageIndex;


/**
* This method sets the language for the R9 library.
*
* @param language(IN)The language index of the language to be set.
* @param Status	(OUT)The success/error status.
*
* @return void
*/
void reverieSetLanguage(LanguageIndex language,int *Status);


/**
 * This method returns the character for the input keycode for the set language.
 *
 * @param keyCode(IN)The keyCode for which the character is to be returned.
 * @param TapCount(IN)The tapcount number on the key
 * @param prevUnichar(IN)The Unicode character preceding the cursor position.
 *If no character is present then please send 0.
 *
 * @param Status(OUT)The success/error status
 *
 * @return The unicode character on that Key.
 */
extern "C"
unsigned short getReverieKeyChar(int keyCode,int TapCount,
        unsigned short prevUnichar, int *Status);



/**
 * This method returns the number of characters for the input keycode for the set language.
 *
 * @param keyCode(IN)The keyCode for which the characters is to be returned.
 * @param prevUnichar(IN)The Unicode character preceding the cursor position.
 *If no character is present then please send 0.
 * @param Status(OUT)The success/error status
 *
 * @returnThe number of unicode characters for the input keycode.
*/

unsigned int getReverieKeyCharCount(int keyCode,unsigned short prevUnichar,int *Status);

/**
 * Sets if half words are to be returned by R9 library.
 *
 * @param isHalf(IN)isHalf is 0 is only full words are to be
 * returned and 1 is half words is to be returned.
 *
 * @return void
 *
 */
void reverieSetHalfWord(char isHalf);



/**
 * Returns the predicted words for the keycode Sequence for the set language.
 *
 * @paramkeycodeSeq(IN)The number sequence for which words are to be predicted.
 * @param words(OUT) The predicted words. It is assumed that at max 30
 * words will be returned. A word cannot be more than length 30.
 * @param numberOfWords(OUT)The number of words returned.
 * @paramstatus(OUT)The Success/Error Status.
 *
 * @returnvoid
 *
 */

void reverieGetPredictedWords(int* keycodeSeq, int keycodeLen, unsigned short words[30][30],
        int * numberOfWords, int * status);

/**
 * Adds a custom word to the R9 library.
 *
 * @paramword(IN)Word to be added to custom dictionary. The word pointer is null terminated.
 * @param status(OUT)The Success/Error Status.
 *
 * @return void
 *
 */
void reverieAddCustomWord(unsigned short * word, int wordLeng, int * status);


/**
 * Deletes a custom word from the R9 library.
 *
 * @param wordToBeDeleted(IN)Word to be added to custom dictionary.
 * The word pointer is null terminated.
 * @param status(OUT)The Success/Error Status.
 * Returns error code in case word was not present in custom word list.
 *
 * @return void
 *
 */
void reverieDeleteCustomWord(unsigned short * wordToBeDeleted, int wordLeng, int * status);


/**
 * Returns the number associated with the given keycode.
 *
 * @param keycode(IN)Word to be added to custom dictionary.
 * The word pointer is null terminated.
 *
 * @return The unicode value of the number associated with the given keycode.
 *
 */

unsigned short reverieGetNumberForKeycode(int keycode);


/**
 * Sets the mode for the R9 library.
 *
 * @param mode(IN)Mode is 0 for abc, 1 for Abc and 2 for ABC.
 *Mode has no effect on Indic languages.
 *
 * @return void
 *
 */
void reverieSetMode(char mode);
#ifdef __cplusplus
}
#endif
