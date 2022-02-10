import argparse
from math import ceil, log, pow

parser = argparse.ArgumentParser(description="This is nuts!")
parser.add_argument("-t", "--type")
parser.add_argument("-p", "--principal")
parser.add_argument("-i", "--interest")
parser.add_argument("-y", "--payment")
parser.add_argument("-e", "--periods")

args = parser.parse_args()

if args.type not in ('annuity', 'diff') or not args.interest:
    print("Incorrect parameters")
else:
    try:
        if args.type == 'diff':
            months = int(args.periods)
            principal = int(args.principal)
            interest = float(args.interest) / 1200
            overpaid = principal
            for i in range(1, months + 1):
                value = (principal / months) + interest * (principal - (principal * (i - 1) / months))
                overpaid -= ceil(value)
                print(f"Month {i}: payment is {ceil(value)}")
            print(f"\nOverpayment = {-overpaid}")
        else:
            if not args.periods:
                principal = int(args.principal)
                monthly_payment = int(args.payment)
                interest = float(args.interest) / 1200
                period = ceil(log(monthly_payment / (monthly_payment - interest * principal), 1 + interest))
                years, months = period // 12, period % 12
                years = f'{years} year{"s" if years > 1 else ""}' if years else ''
                months = f'{months} year{"s" if months > 1 else ""}' if months else ''
                time = f"{years} and {months}" if years and months else years or months
                print(f"\nIt will take {time} to repay the loan!")
                overpaid = (monthly_payment * period) - principal
                print(f"\nOverpayment = {overpaid}")
            elif not args.payment:
                principal = int(args.principal)
                months = int(args.periods)
                interest = float(args.interest) / 1200
                monthly_payment = ceil((principal * interest * pow(1 + interest, months)) / (pow(1 + interest, months) - 1))
                remainder = principal % monthly_payment
                print(f"Your annuity payment = {monthly_payment}!", end='')
                overpaid = (monthly_payment * months) - principal
                print(f"\nOverpayment = {overpaid}")
            elif not args.principal:
                monthly_payment = float(args.payment)
                months = int(args.periods)
                interest = float(args.interest) / 1200
                principal = ceil(monthly_payment / ((interest * pow(1 + interest, months)) / (pow(1 + interest, months) - 1)))
                print(f"Your loan principal = {principal}")
                overpaid = (monthly_payment * months) - principal
                print(f"\nOverpayment = {overpaid}")
    except TypeError:
        print("Incorrect parameters")
