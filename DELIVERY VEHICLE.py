import itertools
import tkinter as tk

# Define the distances between cities
distances = [[0, 7, 9, 16, 18, 30, 31, 38, 22, 31],
             [7, 0, 12, 9, 14, 28, 21, 30, 17, 21],
             [9, 12, 0, 18, 7, 19, 27, 26, 25, 33],
             [16, 9, 18, 0, 14, 16, 23, 26, 12, 21],
             [18, 14, 7, 14, 0, 14, 17, 14, 23, 25],
             [30, 28, 19, 16, 14, 0, 14, 7, 36, 29],
             [31, 21, 27, 23, 17, 14, 0, 9, 33, 23],
             [38, 30, 26, 26, 14, 7, 9, 0, 40, 28],
             [22, 17, 25, 12, 23, 36, 33, 40, 0, 19],
             [31, 21, 33, 21, 25, 29, 23, 28, 19, 0]]


res_dist = ""

class TSP_GUI:
    def __init__(self, master):
        self.master = master
        master.title("Travelling Salesperson Problem")

        self.label_cities = tk.Label(master, text="Number of cities:")
        self.label_cities.pack()

        self.entry_cities = tk.Entry(master)
        self.entry_cities.pack()

        self.button_cities = tk.Button(master, text="Enter", command=self.ask_city_names)
        self.button_cities.pack()

        self.city_names = []


# Define a function to calculate the distance between two cities
def distance(city1, city2):
    return distances[city1][city2]


# Define a function to calculate the total distance of a path
def path_distance(path):
    return sum(distance(path[i], path[(i + 1) % len(path)]) for i in range(len(path)))


# Define a function to find the shortest path between cities
def shortest_path(cities):
    shortest = None
    shortest_len = float('inf')
    for path in itertools.permutations(cities):
        dist = path_distance(path)
        if dist < shortest_len:
            shortest = path
            shortest_len = dist
    return shortest, shortest_len


def calculate_path(all_path_label=None):
    global res_dist
    # Get the names of the cities from the entry widgets
    cities = []
    for i in range(len(city_entries)):
        name = city_entries[i].get()
        if name:
            cities.append(name)

    # Find the shortest path and display it
    if len(cities) >= 2:

        # Calculate shortest path and distance between every pair of cities
        distances = []
        for i in range(len(cities)):
            row = []
            for j in range(len(cities)):
                if i == j:
                    row.append(0.0)
                else:
                    path, dist = shortest_path([i, j])
                    row.append(dist)
            distances.append(row)

        # Display shortest distance between every pair of cities
        for i in range(len(cities)):
            for j in range(i + 1, len(cities)):
                dist = distances[i][j]
                path_str = f"{cities[i]} -> {cities[j]}"
                distance_label = tk.Label(root, text=f"distance between {path_str}: {dist:.2f}" ,font=("Arial",9))
                distance_label.pack(pady=2)

        # Calculate and display shortest path and distance for all cities
        # Create a label widget to display the overall shortest path for all cities
        all_path_label = tk.Label(root, text="", font=("Arial", 15))
        all_path_label.pack(pady=10)

        # Calculate and display shortest path and distance for all cities
        path, dist = shortest_path(range(len(cities)))
        path_str = " -> ".join([cities[i] for i in path])
        all_path_label.config(text=f"Shortest path for all cities: Adalath -> {path_str} -> Adalath")
        distance_label.config(text=f"Shortest distance: {dist:.2f}")

        # Create a label widget to display the overall shortest distance
        overall_dist_label = tk.Label(root, text="", font=("Arial", 15))
        overall_dist_label.pack(pady=10)

        # Display overall shortest distance in a separate label widget
        overall_dist_label.config(text=f"Overall shortest distance: {dist:.2f}")

    else:
        distance_label.config(text="Enter at least two cities to calculate.")
        all_path_label.config(text="")
        all_dist_label.config(text="")



# Create the main window
root = tk.Tk()
root.title("Travelling Salesman Problem")
root.geometry("500x500")
heading_label = tk.Label(root, text="SCHEDULING INTERFACE FOR ELECTRIC DELIVERY VEHICLES", font=("Arial", 22, "underline"))
heading_label.pack(pady=7)
# Second label widget with smaller font size
subheading_label = tk.Label(root, text="This interface considers starting point as Adalath", font=("Arial", 10))
subheading_label.pack(pady=3)

# Create a frame for the city entry widgets
city_frame = tk.Frame(root)
city_frame.pack(padx=13, pady=13)

# Create the city entry widgets
city_entries = []
for i in range(10):
    if i < 5:
        col = 0
    else:
        col = 2
        i -= 5
    label = tk.Label(city_frame, text=f"Location -", font=("Arial", 15))
    label.grid(row=i, column=col, padx=10, pady=10, sticky="e")
    entry = tk.Entry(city_frame)
    entry.grid(row=i, column=col+1, padx=10, pady=10)
    city_entries.append(entry)
    city_frame.grid_columnconfigure(0, weight=1)
    city_frame.grid_columnconfigure(1, weight=1)

    # Define the function that will be called when the "Check Range" button is click
    def check_range():
        #path, dist = shortest_path(range(len(cities)))
        distance = float(res_dist)
        range = float(entry_range.get())
#if loop
        if range >= distance:
            result_label.config(text="You can proceed!", fg="green")
        else:
            result_label.config(text="You will run out of battery..you need to charge your vehicle.", fg="red")


def function1_and_2():
    calculate_path()
    check_range()

# Create a label widget to prompt the user to enter the vehicle range and add it to the window
range_label = tk.Label(root, text="Enter the vehicle range", font=("Arial", 15))
range_label.pack(anchor='center', pady=10)
entry_range = tk.Entry(root)
entry_range.pack(anchor='s')


# Create a button widget to allow the user to check if the range is sufficient and add it to the window
check_button = tk.Button(root, text="submit", font=("Arial", 13), command=function1_and_2, width=8, height=1)
check_button.pack()

# Create a label to display the shortest path
distance_label = tk.Label(root, text="", font=("Arial", 15))
distance_label.pack()

# Create a label widget to display the result of the range check and add it to the window
result_label = tk.Label(root, text="", font=("Arial", 15))
result_label.pack()

# Start the main loop
root.mainloop()