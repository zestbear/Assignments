#include <stdio.h>
#include <stdlib.h>
#define MAX_DEGREE 9

typedef struct{
    int degree; // 다항식의 최고차항
    int exp[MAX_DEGREE]; // 지수의 배열
    int coef[MAX_DEGREE]; // 계수의 배열
}polynomial;

polynomial polyA(){ // A(x) 입력받기
    polynomial A;
    printf("A(x)의 최고차항 입력 : ");
    scanf("%d",&A.degree);
    if(A.degree>9||A.degree<0) exit(0);
    printf("계수 오름차순으로 입력\n");
    for(int i=0;i<A.degree+1;i++){
        printf("%d의 계수 : ",i);
        scanf("%d",&A.coef[i]);
        A.exp[i]=i;
    }
    return A;
}

polynomial polyB(){ // B(x) 입력받기
    polynomial B;
    printf("B(x)의 최고차항 입력 : ");
    scanf("%d",&B.degree);
    if(B.degree>9||B.degree<0) exit(0);
    printf("계수 오름차순으로 입력\n");
    for(int i=0;i<B.degree+1;i++){
        printf("%d의 계수 : ",i);
        scanf("%d",&B.coef[i]);
        B.exp[i]=i;
    }
    return B;
}

void showpoly(polynomial p) { // 함수 출력하기
    int idx=0;
    while(idx<=p.degree){
        printf("%dx^%d ",p.coef[idx],idx);
        if(p.coef[idx+1]>=0 && idx+1<=p.degree){
            printf("+");
        }
        idx++;
    }
    printf("\n");
}

typedef struct term *ptr2term;
typedef struct term{
    int coef;
    int exp;
    ptr2term link;
} _term;

ptr2term D;
void polymult(polynomial p, polynomial q){
    D=(ptr2term)malloc(sizeof(*D));
    D->link=NULL;
    D->exp=-1;
    for(int i=0;i<p.degree+1;i++){
        for(int j=0;j<q.degree+1;j++){
            ptr2term newnode;
            ptr2term rear;
            ptr2term a=D;
            while(a->link!=NULL){
                a=a->link;
            }
            rear=a;
            newnode=(ptr2term)malloc(sizeof(*newnode));
            if(p.coef[i]!=0 && q.coef[j]!=0){
                ptr2term t=D;
                int cnt=0;
                while(t!=NULL){
                    if(t->exp==i+j){
                        cnt++;
                    }
                    t=t->link;
                }
                if(cnt==0){
                    newnode->exp=i+j;
                    newnode->coef=p.coef[i]*q.coef[j];
                    newnode->link=NULL;
                    if(D->link==NULL){ // i+j가 없으며 D가 empty
                        D->link=newnode;
                    }
                    else{ // i+j가 없으며 D가 empty가 아님
                        ptr2term t=D;
                        while((t->link)!=NULL){
                            ptr2term tn=t->link;
                            if(tn->exp < i+j){ // 노드를 처음, 중간에 삽입
                                newnode->link=t->link;
                                t->link=newnode;
                                break;
                            }
                            t=t->link;
                        }
                        while(t!=NULL){
                            if(t->link==NULL){
                                if(t->exp > i+j){
                                    t->link=newnode;
                                }
                            }
                            t=t->link;
                        }
                    }
                }
                else{ // cnt > 0
                    ptr2term t=D;
                    while((t->link)!=NULL){
                        ptr2term tn=t->link;
                        if(tn->coef+p.coef[i]*q.coef[j]==0){
                            if(tn->link==NULL){
                                t->link=NULL;
                                free(tn);
                            }
                            else{
                                t->link=tn->link;
                                free(tn);
                            }
                        }
                        if(tn->exp==i+j){
                            tn->coef+=p.coef[i]*q.coef[j];
                        }
                        t=t->link;
                    }
                }
            }
        }
    }
    ptr2term k=D->link;
    while(k!=NULL){
        if(k->coef > 0){
            printf("+");
        }
        printf("%dx^%d ",k->coef,k->exp);
        k=k->link;
    }
    printf("\n");
}

int main(){
    polynomial a,b;
    for(int i=0;i<3;i++){
        printf("------------%d번째------------\n",i+1);
        a = polyA();
        b = polyB();
        
        printf("A(x) : ");
        showpoly(a);
        printf("B(x) : ");
        showpoly(b);
        printf("D(x) : ");
        polymult(a,b);
    }
    return 0;
}

