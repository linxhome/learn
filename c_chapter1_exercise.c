#include <stdio.h>
#define LOWER 0
#define UPPER 300
#define STEP 20
int a1_5();
int a1_6();
int a1_7();
int a1_8();
int a1_9();
int a1_10();

int main() {
    //a1_5();
    //a1_6();
    //a1_7();
    //a1_8();
    //a1_9();
//    a1_10();
printf("|||||------");
}


int a1_5() {
    float fahr,celsius;
    int lower,upper,step;

    fahr = LOWER;
    while(fahr <= UPPER) {
        celsius = 5 * (fahr -32) / 9;
        printf("fahr is %3.2f and celsius is %3.2f \n",fahr,celsius);
        fahr += STEP;
    }
    return -1;
}

int a1_6() {
    int value = (int)(1==1);
    printf("EOF int value is %d \n",value);
    return 0;
}

int a1_7() {
    printf("EOF is %d \n",EOF);
    return 0;
}

int a1_8() {
    printf("get the num of the empty and tab");
    int cha;
    int tab=0;int space= 0 ;int line = 0;
    while((cha = getchar()) != EOF) {
        switch(cha) {
            case '\t':
                tab++;
                break;
            case '\n':
                line++;
                break;
            default:
                break;
        }
    }
    printf("tab has %d , space has %d,line has %d \n",tab,space,line);
    return 0;
}

int a1_9() {
    int space = 0;
    char cha;
    while((cha = getchar()) != EOF) {
        if(cha == ' ') {
            space++;
        }
        else {
            if(space>0) {
                putchar(' ');
                space = 0;
            }
            putchar(cha);
        }
    }
}

int a1_10() {
    char cha;
    int line = 0;
    while((cha = getchar()) != EOF) {
        if(cha == ' ') {
            line++;
        }
        else {
            if(line >0) {
                putchar('\n');
                line = 0;
            }
            putchar(cha);
        }
    }
}

